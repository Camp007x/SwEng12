package de.shelp.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;

import org.jboss.logging.Logger;
import org.jboss.ws.api.annotation.WebContext;

import de.shelp.dao.local.ShelpRequestDAOLocal;
import de.shelp.dao.local.ShelpTourDAOLocal;
import de.shelp.dao.local.ShelpUserDAOLocal;
import de.shelp.dto.ReturnCodeResponse;
import de.shelp.dto.request.RequestTO;
import de.shelp.dto.request.RequestsResponse;
import de.shelp.entities.Request;
import de.shelp.entities.ShelpSession;
import de.shelp.entities.Tour;
import de.shelp.entities.User;
import de.shelp.entities.WishlistItem;
import de.shelp.enums.RequestStatus;
import de.shelp.enums.ReturnCode;
import de.shelp.exception.SessionNotExistException;
import de.shelp.exception.ShelpException;
import de.shelp.exception.TourNotExistException;
import de.shelp.util.RequestDtoAssembler;
import de.shelp.util.ShelpHelper;

/**
 * @author anwender
 * Webservice-Schnittstelle f�r die Behandlung von Anfragen
 *
 */
@WebService
@WebContext(contextRoot = "/shelp")
@Stateless
public class RequestIntegration {

	private static final Logger LOGGER = Logger
			.getLogger(RequestIntegration.class);

	/**
	 * EJB zur Abfrage von Datens�tzen Referenz auf die EJB wird per Dependency
	 * Injection gef�llt.
	 */
	@EJB(beanName = "ShelpUserDAO", beanInterface = ShelpUserDAOLocal.class)
	private ShelpUserDAOLocal daoUser;

	/**
	 * EJB zur Abfrage von Datens�tzen Referenz auf die EJB wird per Dependency
	 * Injection gef�llt.
	 */
	@EJB(beanName = "ShelpRequestDAO", beanInterface = ShelpRequestDAOLocal.class)
	private ShelpRequestDAOLocal daoRequest;

	/**
	 * EJB zur Erzeugung von DataTransferObjects
	 */
	@EJB
	private RequestDtoAssembler requestDtoAssembler;

	/**
	 * EJB zur Beauftragung zum Versenden von E-Mails
	 */
	@EJB
	private MailRequesterBean mailRequester;

	/**
	 * EJB zur Abfrage von Datens�tzen Referenz auf die EJB wird per Dependency
	 * Injection gef�llt.
	 */
	@EJB(beanName = "ShelpTourDAO", beanInterface = ShelpTourDAOLocal.class)
	private ShelpTourDAOLocal tourDao;

	@EJB
	private ShelpHelper helper;

	/**
	 * Akzeptiert eine Anfrage
	 * 
	 * @param requestId
	 *            ID der Anfrage
	 * @param acceptedIds
	 * @param sessionId
	 *            Aktuelle SessionID
	 * @return AntwortCode
	 */
	public ReturnCodeResponse acceptRequest(long requestId, String acceptedIds,
			int sessionId) {

		ReturnCodeResponse response = new ReturnCodeResponse();

		try {
			Request request = checkRequest(sessionId, requestId, false);

			if (!request.getStatus().equals(RequestStatus.ASKED)) {
				LOGGER.warn("Anfrage wurde schon angenommen/abgelehnt");
				throw new ShelpException(ReturnCode.ERROR,
						"Anfrage wurde schon angenommen/abgelehnt");
			}

			boolean acceptOneItem = false;
			boolean acceptAllItem = true;

			List<WishlistItem> wishes = request.getWishes();
			ArrayList<String> ids = new ArrayList<String>(
					Arrays.asList(acceptedIds.split("\n")));
			for (WishlistItem wishlistItem : wishes) {
				if (ids.contains(String.valueOf(wishlistItem.getId()))) {
					acceptOneItem = true;
					wishlistItem.setChecked(true);
				} else {
					acceptAllItem = false;
				}
			}
			request.setUpdated(true);

			if (!acceptOneItem) {
				request.setStatus(RequestStatus.DENIED);
			} else if (acceptAllItem) {
				request.setStatus(RequestStatus.ACCEPT);
			} else {
				request.setStatus(RequestStatus.PARTLY_ACCEPT);
			}

			String logMessage = "Die Anfrage zur Fahrt "
					+ request.getTour().getLocation()
					+ " wurde auf den Status " + request.getStatus()
					+ " gesetzt.";
			LOGGER.info(logMessage);
			mailRequester.printLetter(logMessage, request.getSourceUser()
					.getEmail());

			daoRequest.persistRequest(request);

		} catch (ShelpException e) {
			response.setReturnCode(e.getErrorCode());
			response.setMessage(e.getMessage());
		}

		return response;
	}

	/**
	 * Erstellt eine Anfrage
	 * 
	 * @param targetUserId UserID des Angefragten
	 * @param tourId Aktuelle TourId
	 * @param notice Notiz des Anfragenden
	 * @param sessionId Aktuelle SessionID
	 * @param wishes Einkaufsw�nsche
	 * @return returnCodeResponse 
	 * @throws SessionNotExistException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ReturnCodeResponse createRequest(long tourId, String notice,
			int sessionId, String wishes) {

		// create empty response
		ReturnCodeResponse response = new ReturnCodeResponse();

		try {
			// check current Session
			ShelpSession session = helper.checkSession(sessionId, daoUser);

			// get current Tour
			Tour tour = tourDao.getTour(tourId);

			if (tour == null) {
				String message = "Fahrt existiert nicht.";
				LOGGER.info(message);
				throw new TourNotExistException(message);
			}

			// get targetUser
			User targetUser = tour.getOwner();

			if (targetUser.equals(session.getUser())) {
				String message = "Sich selbst eine Anfrage senden, ergibt doch gar keinen Sinn lieber "
						+ session.getUser();
				LOGGER.info(message);
				throw new ShelpException(ReturnCode.ERROR, message);
			}

			// create request if parameter are ok
			Request request = new Request();
			request.setSourceUser(session.getUser());
			request.setTargetUser(targetUser);
			request.setTour(tour);
			request.setNotice(notice);
			request.setUpdated(false);
			request.setStatus(RequestStatus.ASKED);

			// create list for wishlistitems
			List<WishlistItem> wishlistItems = new ArrayList<WishlistItem>();

			String[] split = wishes.split("\n");

			for (String string : split) {
				WishlistItem item = new WishlistItem();
				item.setText(string);
				item.setChecked(false);
				item.setOwner(request);
				wishlistItems.add(item);
			}

			// set created wishlist to request
			request.setWishes(wishlistItems);

			// create request
			daoRequest.persistRequest(request);

			// update time for tour
			tour.setUpdated(true);

			// save tour
			tourDao.saveTour(tour);

			String logMessage = "Zu der Fahrt " + tour.getLocation() + " ("
					+ tour.getId() + ") wurde eine Anfrage gestellt.";
			LOGGER.info(logMessage);
			mailRequester.printLetter(logMessage, tour.getOwner().getEmail());

		} catch (ShelpException e) {
			response.setReturnCode(e.getErrorCode());
			response.setMessage(e.getMessage());
		}

		return response;
	}

	/**
	 * Gibt Anfragen zur�ck
	 * @param sessionId
	 * @return
	 */
	public RequestsResponse getRequests(int sessionId) {
		RequestsResponse response = new RequestsResponse();
		try {
			ShelpSession session = helper.checkSession(sessionId, daoUser);

			List<Request> requests = session.getUser().getOwnRequests();
			List<RequestTO> dtoRequests = new ArrayList<RequestTO>();
			for (Request request : requests) {
				if (!request.getStatus().equals(RequestStatus.REMOVED)) {
					dtoRequests.add(requestDtoAssembler.makeDTO(request));
				}
			}

			LOGGER.info(requests.size() + " Anfragen gefunden.");

			response.setRequests(dtoRequests);
		} catch (SessionNotExistException e) {
			response.setReturnCode(e.getErrorCode());
			response.setMessage(e.getMessage());
		}

		return response;
	}

	/**
	 * L�scht eine Anfrage
	 * 
	 * @param requestId ID der zu l�schenden Anfrage
	 * @param sessionId Aktuelle SessionID
	 * @return ReturnCodeResponse
	 */
	public ReturnCodeResponse deleteRequest(long requestId, int sessionId) {

		// create empty response
		ReturnCodeResponse response = new ReturnCodeResponse();

		try {
			// check request
			Request request = checkRequest(sessionId, requestId, true);

			// delete request
			daoRequest.deleteRequest(request);
			LOGGER.info("Anfrage wurde gel�scht.");

		} catch (ShelpException e) {
			response.setReturnCode(e.getErrorCode());
			response.setMessage(e.getMessage());
		}

		return response;
	}

	/**
	 * Gibt aktualisierte Anfrage zur�ck
	 * 
	 * @param sessionId Aktuelle Session Id
	 * @return 
	 * @throws SessionNotExistException
	 */
	public RequestsResponse getUpdatedRequests(int sessionId) {

		// create empty response
		RequestsResponse response = new RequestsResponse();
		try {
			// check current session
			ShelpSession session = helper.checkSession(sessionId, daoUser);

			// get all request of the user
			List<Request> requests = session.getUser().getOwnRequests();

			List<RequestTO> resultList = new ArrayList<RequestTO>();
			// check if tour has updated
			for (Request request : requests) {
				if (request.isUpdated()) {
					// request aufnehmen und wieder als nicht aktualisiert
					// abspeichern
					resultList.add(requestDtoAssembler.makeDTO(request));
					request.setUpdated(false);
					daoRequest.persistRequest(request);
				}
			}
			response.setRequests(resultList);
			LOGGER.info(requests.size() + " aktualisierte Anfragen gefunden.");
		} catch (ShelpException e) {
			response.setReturnCode(e.getErrorCode());
			response.setMessage(e.getMessage());
		}

		return response;
	}

	/**
	 * �berpr�ft eine Anfrage auf G�ltigkeit
	 * 
	 * @param sessionId Aktuelle SessionId
	 * @param requestId ID der zu �berpr�fenden Anfrage
	 * @param checkBoth Steuerparameter f�r die �berpr�fung
	 * @return
	 * @throws ShelpException
	 */
	private Request checkRequest(int sessionId, long requestId,
			boolean checkBoth) throws ShelpException {

		// get current Session
		ShelpSession session = helper.checkSession(sessionId, daoUser);

		// get user from session
		User user = session.getUser();

		// get current request
		Request request = daoRequest.getRequestById(requestId);

		// check request
		if (request == null) {
			LOGGER.info("Anfrage nicht g�ltig.");
			throw new ShelpException(ReturnCode.ERROR,
					"Anfrage existiert nicht!");
		}

		if ((checkBoth && (request.getTargetUser().equals(user) || request
				.getSourceUser().equals(user)))
				|| !checkBoth
				&& request.getTargetUser().equals(user)) {

			return request;

		} else {
			LOGGER.info("Anfrage nicht erlaubt.");
			throw new ShelpException(ReturnCode.PERMISSION_DENIED,
					"Anfrage nicht erlaubt!");
		}
	}
}
