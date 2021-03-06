package de.shelp.schedules;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jboss.logging.Logger;

import de.shelp.dao.local.ShelpFriendDAOLocal;
import de.shelp.dao.local.ShelpTourDAOLocal;
import de.shelp.dao.local.ShelpUserDAOLocal;
import de.shelp.entities.Friendship;
import de.shelp.entities.ShelpSession;
import de.shelp.entities.Tour;

@Singleton
@Startup
public class ShelpSchedules {

    private static final Logger LOGGER = Logger.getLogger(ShelpSchedules.class);

    /**
     * EJB zur Abfrage von Datens�tzen Referenz auf die EJB wird per Dependency
     * Injection gef�llt.
     */
    @EJB(beanName = "ShelpUserDAO", beanInterface = ShelpUserDAOLocal.class)
    private ShelpUserDAOLocal userDao;

    /**
     * EJB zur Abfrage von Datens�tzen Referenz auf die EJB wird per Dependency
     * Injection gef�llt.
     */
    @EJB(beanName = "ShelpTourDAO", beanInterface = ShelpTourDAOLocal.class)
    private ShelpTourDAOLocal tourDao;

    /**
     * EJB zur Abfrage von Datens�tzen Referenz auf die EJB wird per Dependency
     * Injection gef�llt.
     */
    @EJB(beanName = "ShelpFriendDAO", beanInterface = ShelpFriendDAOLocal.class)
    private ShelpFriendDAOLocal friendDao;

    // Immer um 2 Uhr nachts
    @Schedule(hour = "2")
    public void removeUnneededSessions() {
	List<ShelpSession> sessions = userDao.getSessions();
	LOGGER.info(sessions.size()
		+ " exstierende Sessions werden auf ihre G�ltigkeit gepr�ft");

	for (ShelpSession shelpSession : sessions) {
	    // Alle Sessions die eine Stunde nichts getan haben werden entfernt
	    if (shelpSession.getUpdatedOn().before(
		    new Date(new Date().getTime() - 3600000))) {
		LOGGER.info(shelpSession.getId()
			+ " ist abgelaufen und wird entfernt.");
		userDao.closeSession(shelpSession.getId());
	    }

	}
    }

    // Jede Stunde einmal
    @Schedule(hour = "*", minute = "0,5,10,15,20,25,30,35,40,45,50,55")
    public void relaisedTours() {
	List<Tour> tours = tourDao.getOpenTours();
	LOGGER.info(tours.size()
		+ " exstierende Fahrten werden auf ihre G�ltigkeit gepr�ft");

	for (Tour tour : tours) {
	    // Alle Fahrent die vorr�ber sind werden abgeschlossen
	    if (tour.getTime().before(new Date())) {
		LOGGER.info(tour
			+ " ist vorbei und wird auf abgeschlossen gesetzt.");
		tourDao.closeTour(tour);
	    }
	}
    }

    // immer Sonntags nachts
    @Schedule(dayOfWeek = "Sun", hour = "2")
    public void deleteDeniedFriendships() {
	List<Friendship> friendships = friendDao.getDeniedFriendships();
	LOGGER.info(friendships.size()
		+ " abgelehnte Freundschaften werden entfernt");

	for (Friendship friendship : friendships) {
	    // Alle abgelehnten Freundschaftsanfragen werden entfernt
	    LOGGER.info(friendship
			+ " wurde abgelehnt und wird entg�ltig entfernt.");
	    friendDao.deleteFriendship(friendship);
	}
    }

}
