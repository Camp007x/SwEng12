package de.shelp.integration;

import java.util.ArrayList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.jboss.logging.Logger;
import org.jboss.ws.api.annotation.WebContext;

import de.shelp.dao.local.ShelpUserDAOLocal;
import de.shelp.dto.ReturnCodeResponse;
import de.shelp.dto.user.UserResponse;
import de.shelp.dto.user.UserTO;
import de.shelp.dto.user.UsersResponse;
import de.shelp.entities.ShelpSession;
import de.shelp.entities.User;
import de.shelp.enums.ReturnCode;
import de.shelp.exception.InvalidLoginException;
import de.shelp.exception.SessionNotExistException;
import de.shelp.exception.ShelpException;
import de.shelp.exception.UserNotExistException;
import de.shelp.util.UserDtoAssembler;

/**
 * Webservice der alle n�tigen Methoden zur Benutzer-/Sessionverwaltung
 * bereitstellt. �ber die Schnittstelle k�nnen Benutzer angelegt
 * {@link #regUser(String, String)}, eingeloggt {@link #login(String, String)},
 * ausgeloggt {@link #logout(int)} und gesucht {@link #searchUsers(String)}
 * werden.
 * 
 * <br>
 * Jeder Schritt wird �ber die Logausgabe dokumentiert. Au�erdem werden alle
 * Entit�ten vor der R�ckgabe in Data Transfer Objekte umgewandelt.
 * 
 * @author Jos Werner
 *
 */
@WebService
@WebContext(contextRoot = "/shelp")
@Stateless
public class UserIntegration {

    private static final Logger LOGGER = Logger
	    .getLogger(UserIntegration.class);

    /**
     * EJB zur Abfrage von Datens�tzen der Benutzer. Referenz auf die EJB wird
     * per Dependency Injection gef�llt.
     */
    @EJB(beanName = "ShelpUserDAO", beanInterface = ShelpUserDAOLocal.class)
    private ShelpUserDAOLocal dao;

    /**
     * EJB zur Erzeugung von DataTransferObjects der Benutzer
     */
    @EJB
    private UserDtoAssembler dtoAssembler;

    /**
     * EJB zur Beauftragung zum Versenden von E-Mails
     */
    @EJB
    private MailRequesterBean mailRequester;

    /**
     * Schnittstelle die genutzt werden kann um einen neuen Benutzer (
     * {@link User}) zu registrieren. Das Passwort sollte verschl�sselt an den
     * Server verschickt werden. Es wird gepr�ft ob die E-Mailadresse g�ltig ist
     * und ob der Benutzer schon existiert. Sollte dies der Fall sein wird der
     * {@link ReturnCode} ERROR zur�ckgegeben. Kann der Benutzer angelegt werden
     * wird direkt eine neue {@link ShelpSession} f�r den Benutzer angelegt und
     * auf der Datenbank abgespeichert.
     * 
     * @param email
     *            - die E-Mailadresse des neuen Benutzers
     * @param password
     *            - das verschl�sselte Passwort des neuen Benutzers
     * @return einen {@link UserResponse} mit der neuen Session und
     *         {@link ReturnCode} OK oder {@link ReturnCode} ERROR +
     *         Fehlermeldung
     */
    public UserResponse regUser(String email, String password) {
	UserResponse response = new UserResponse();
	try {
	    if (!validEMail(email)) {
		LOGGER.warn("E-Mail nicht g�ltig.");
		throw new ShelpException(ReturnCode.ERROR,
			"E-Mail nicht g�ltig.");
	    }

	    User user = this.dao.findUserByName(email);
	    if (user == null) {
		user = this.dao.createUser(password, email);
		ShelpSession session = dao.createSession(user);
		String logMessage = "Benutzer " + user
			+ " erfolgreich angelegt.";
		LOGGER.info(logMessage);
		mailRequester.printLetter(logMessage, email);
		response.setSession(dtoAssembler.makeDTO(session));
	    } else {
		LOGGER.info("Registrierung fehlgeschlag. Benutzername existiert schon "
			+ user);
		throw new UserNotExistException(
			"Registrierung fehlgeschlag. Benutzername schon vergeben.");
	    }
	} catch (ShelpException e) {
	    response.setReturnCode(e.getErrorCode());
	    response.setMessage(e.getMessage());
	}
	return response;
    }

    /**
     * Schnittstelle um einen existierenden Benutzer ({@link User}) einzuloggen.
     * Es wird gepr�ft ob der Benutzer existiert und ob das Passwort mit dem
     * Passwort des Benutzer �bereinstimmt. Falls nicht wird der
     * {@link ReturnCode} ERROR zur�ckgegeben. Kann der Benutzer erfolgreich
     * eingeloggt werden, wird eine neue {@link ShelpSession} angelegt und in
     * der Datenbank abgespeichert.
     * 
     * @param email
     *            - E-Mail des einzuloggenden Benutzers
     * @param password
     *            - Passwort des einzuloggenden Benutzers
     * @return {@link UserResponse} mit der neuen Session und {@link ReturnCode}
     *         OK oder {@link ReturnCode} ERROR + Fehlermeldung
     */
    public UserResponse login(String email, String password) {
	UserResponse response = new UserResponse();
	try {
	    User user = this.dao.findUserByName(email);
	    if (user != null && user.getPassword().equals(password)) {
		ShelpSession session = dao.createSession(user);
		LOGGER.info("Login erfolgreich. Session=" + session);
		response.setSession(dtoAssembler.makeDTO(session));
	    } else {
		LOGGER.info("Login fehlgeschlagen, da Benutzer unbekannt oder Passwort falsch. username="
			+ email);
		throw new InvalidLoginException(
			"Login fehlgeschlagen, da Benutzer unbekannt oder Passwort falsch. username="
				+ email);
	    }
	} catch (ShelpException e) {
	    response.setReturnCode(e.getErrorCode());
	    response.setMessage(e.getMessage());
	}
	return response;
    }

    /**
     * Schnittstelle um einen eingeloggten Benutzer ({@link User}) auszuloggen.
     * Pr�ft ob die SessionId existiert und gibt {@link ReturnCode} ERROR zur�ck
     * falls nicht. Andernfalls wird die {@link ShelpSession} aus der Datenbank
     * gel�scht.
     * 
     * @param sessionId
     *            - Id der Session die ausgeloggt werden soll
     * 
     * @return einen {@link ReturnCodeResponse} mit {@link ReturnCode} OK oder
     *         ERROR + Fehlermeldung
     */
    public ReturnCodeResponse logout(int sessionId) {
	ReturnCodeResponse response = new ReturnCodeResponse();
	try {
	    if (!dao.closeSession(sessionId)) {
		LOGGER.info("Logout nicht erfolgreich. Session " + sessionId
			+ " existiert nicht.");
		throw new SessionNotExistException(
			"Logout nicht erfolgreich. Session " + sessionId
				+ " existiert nicht.");
	    } else {
		LOGGER.info("Logout erfolgreich. Session=" + sessionId);
	    }
	} catch (ShelpException e) {
	    response.setReturnCode(e.getErrorCode());
	    response.setMessage(e.getMessage());
	}
	return response;
    }

    /**
     * Schnittstelle um nach Benutzern zu suchen. Der Suchtext kann dabei an
     * einer beliebige Stelle in der E-Mail vorkommen. Ein leerer Suchtext gibt
     * alle Benutzer zur�ck.
     * 
     * @param searchText
     *            - Text nach dem gesucht werden soll
     * @return einen {@link UsersResponse} mit {@link ReturnCode} OK und allen
     *         gefundenen Benutzern
     */
    public UsersResponse searchUsers(String searchText) {
	UsersResponse response = new UsersResponse();

	List<User> users = dao.searchUsers(searchText);

	List<UserTO> usersTO = new ArrayList<UserTO>();
	for (User u : users) {
	    usersTO.add(dtoAssembler.makeDTO(u));
	}

	response.setUserList(usersTO);
	LOGGER.info(usersTO.size() + " Benutzer zu " + searchText
		+ " gefunden.");

	return response;
    }

    /**
     * Interne Methode die �berpr�ft ob eine E-Mail g�ltig ist. �berpr�ft wird
     * ein mit einem Regex-Ausdruck der die meisten g�ngigen E-Mailadressen
     * abf�ngt.
     * 
     * @param email
     *            - die E-Mailadresse die gepr�ft weden soll
     * 
     * @return true wenn die Adresse g�ltig ist, sonst false
     */
    private boolean validEMail(String email) {
	String emailPattern = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@"
		+ "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

	Pattern pattern = Pattern.compile(emailPattern);
	Matcher matcher = pattern.matcher(email);
	return matcher.matches();
    }
}
