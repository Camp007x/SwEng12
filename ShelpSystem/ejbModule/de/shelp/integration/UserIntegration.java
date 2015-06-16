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

@WebService
@WebContext(contextRoot = "/shelp")
@Stateless
public class UserIntegration {

    private static final Logger LOGGER = Logger
	    .getLogger(UserIntegration.class);

    /**
     * EJB zur Abfrage von Datens�tzen Referenz auf die EJB wird per Dependency
     * Injection gef�llt.
     */
    @EJB(beanName = "ShelpUserDAO", beanInterface = ShelpUserDAOLocal.class)
    private ShelpUserDAOLocal dao;

    /**
     * EJB zur Erzeugung von DataTransferObjects
     */
    @EJB
    private UserDtoAssembler dtoAssembler;

    /**
     * EJB zur Beauftragung zum Versenden von E-Mails
     */
    @EJB
    private MailRequesterBean mailRequester;

    public UserResponse regUser(String email, String password) {
	UserResponse response = new UserResponse();
	try {
	    if (!checkEMail(email)) {
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

    private boolean checkEMail(String email) {
	String emailPattern = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@"
		+ "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

	Pattern pattern = Pattern.compile(emailPattern);
	Matcher matcher = pattern.matcher(email);
	return matcher.matches();
    }
}
