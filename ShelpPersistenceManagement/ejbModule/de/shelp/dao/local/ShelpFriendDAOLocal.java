package de.shelp.dao.local;

import java.util.List;

import javax.ejb.Local;

import de.shelp.entities.Friendship;
import de.shelp.enums.FriendshipStatus;

/**
 * Interface das vorgibt welche Methoden f�r die datenbankseitige
 * Freundeverwaltung ben�tigt werden.
 * 
 * @author Thomas Sennekamp
 *
 */
@Local
public interface ShelpFriendDAOLocal {

    /**
     * Persistiert die Freundschaft ({@link Friendship}) in der Datenbank
     * 
     * @param friendship
     *            - zu speicherende Freundschaft
     */
    public void saveFriendship(Friendship friendship);

    /**
     * Sucht eine Freundschaft ({@link Friendship}) in Datenbank nach ihrer Id.
     * 
     * @param friendshipHash
     *            - Id der Freundschaft
     * @return die gefundene Freundschaft oder null
     */
    public Friendship findFriendshipById(int friendshipHash);

    /**
     * L�scht eine Freundschaft ({@link Friendship}) aus der Datenbank raus.
     * 
     * @param friendship
     *            - die zu l�schene Freundschaft
     */
    public void deleteFriendship(Friendship friendship);

    /**
     * Gibt alle Freunschaften ({@link Friendship}) zur�ck die den
     * {@link FriendshipStatus} DENIED haben.
     * 
     * @return eine Liste aller abgelehnten Freundschaften
     */
    public List<Friendship> getDeniedFriendships();

}
