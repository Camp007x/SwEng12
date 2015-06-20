package de.shelp.dao.local;

import javax.ejb.Local;

import de.shelp.entities.Rating;

/**
 * Interface das vorgibt welche Methoden f�r die datenbankseitige
 * Bewertungsverwaltung ben�tigt werden.
 * 
 * @author Thomas Sennekamp
 *
 */
@Local
public interface ShelpRatingDAOLocal {

    /**
     * Speichert eine Bewertung ({@link Rating}) in der Datenbank ab.
     * 
     * @param newRating
     *            - die zu speichernde Bewertung
     */
    public void createRating(Rating newRating);

}
