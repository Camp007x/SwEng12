package de.shelp.dao.local;

import java.util.List;

import javax.ejb.Local;

import de.shelp.entities.ShelpSession;
import de.shelp.entities.User;

/**
 * Interface das vorgibt welche Methoden f�r die datenbankseitige
 * Benutzerverwaltung ben�tigt werden.
 * 
 * @author Jos Werner
 *
 */
@Local
public interface ShelpUserDAOLocal {

    /**
     * Findet einen Benutzer ({@link User}) in der Datenbank anhand seines
     * Namens (E-Mailsadresse)
     * 
     * @param username
     *            - E-Mailadresse des Benutzers
     * 
     * @return den gefunden Benutzer oder null
     */
    public User findUserByName(String username);

    /**
     * Speichert eine Session ({@link ShelpSession}) in die Datenbank ab.
     * 
     * @param user
     *            - Besitzer der Session
     * @return die erstellte Session
     */
    public ShelpSession createSession(User user);

    /**
     * Speichert einen Benutzer ({@link User}) in die Datenbank ab.
     * 
     * @param username
     *            - Name des neuen Benutzers
     * @param password
     *            - Passwort des neuen Benutzers
     * @return den erstellten Benutzer
     */
    public User createUser(String username, String password);

    /**
     * L�scht eine Session ({@link ShelpSession}) aus der Datenbank raus.
     * 
     * @param sessionId
     *            - Id der zu l�schenden Session
     * @return true - wenn sie gel�scht wurde, sonst false
     */
    public boolean closeSession(int sessionId);

    /**
     * Gibt alle Benutzer ({@link User}) zur�ck die den gesuchten Text
     * beinhalten.
     * 
     * @param searchText
     *            - gesuchter Text
     * @return Liste mit passenden Benutzern
     */
    public List<User> searchUsers(String searchText);

    /**
     * Gibt die zu der Id passende Session {@link ShelpSession} zur�ck.
     * 
     * @param sessionId
     *            - gesuchte Id
     * @return gefundene Session oder null
     */
    public ShelpSession getSession(int sessionId);

    /**
     * Gibt alle in der Datenbank hinterlegten Sessions ({@link ShelpSession})
     * zur�ck.
     * 
     * @return Liste von existierenden Sessions
     */
    public List<ShelpSession> getSessions();

    /**
     * Aktualisiert den Zeitstempel einer Session ({@link ShelpSession})
     * 
     * @param session
     *            - die zu aktualisierende Session
     */
    public void updateSession(ShelpSession session);

}
