package de.shelp.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.shelp.dao.local.ShelpTourDAOLocal;
import de.shelp.entities.ApprovalStatus;
import de.shelp.entities.Capacity;
import de.shelp.entities.DeliveryCondition;
import de.shelp.entities.Location;
import de.shelp.entities.PaymentCondition;
import de.shelp.entities.Tour;
import de.shelp.entities.User;
import de.shelp.enums.TourStatus;

/**
 * Session Bean implementation class ShelpTourDAO
 */
@Stateless
public class ShelpTourDAO implements ShelpTourDAOLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Tour createTour(Tour tour, User user) {
	tour.setOwner(user);
	tour.setStatus(TourStatus.PLANED);
	em.persist(tour);
	return tour;
    }

    @Override
    public List<Tour> search(ApprovalStatus approvalStatus, Location location,
	    Date startTime, Date endTime, User currentUser) {
	CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
	CriteriaQuery<Tour> criteriaQuery = criteriaBuilder
		.createQuery(Tour.class);
	Root<Tour> tour = criteriaQuery.from(Tour.class);

	List<Tour> searchedTours = new ArrayList<Tour>();
	// TODO get friends of current user and get their active tours with
	// friends_only state

	// TODO den String Alle sinnvoll auslagern
	if (approvalStatus.getDescription().equals("Alle")) {
	    Predicate andClause = criteriaBuilder.and(criteriaBuilder.equal(
		    tour.<Location> get("location"), location), criteriaBuilder
		    .and(criteriaBuilder.between(tour.<Date> get("time"),
			    startTime, endTime), criteriaBuilder.equal(
			    tour.<ApprovalStatus> get("approvalStatus"),
			    approvalStatus)));
	    criteriaQuery.select(tour);
	    criteriaQuery.where(andClause);
	    searchedTours.addAll(em.createQuery(criteriaQuery).getResultList());
	}

	return searchedTours;
    }

    @Override
    public List<Tour> searchNear(ApprovalStatus approvalStatus,
	    Location location, Date startTime, Date endTime, User currentUser) {
	// Get all locations, filter by zipcode of given location
	CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
	CriteriaQuery<Location> criteriaQuery = criteriaBuilder
		.createQuery(Location.class);
	Root<Location> locationQuery = criteriaQuery.from(Location.class);
	criteriaQuery.select(locationQuery);
	criteriaQuery.where(criteriaBuilder.equal(
		locationQuery.<String> get("zipcode"), location.getZipcode()));

	List<Location> locationList = em.createQuery(criteriaQuery)
		.getResultList();

	// search all tours of location list and at to result
	List<Tour> resultList = new ArrayList<Tour>();

	for (Location locationSearch : locationList) {
	    resultList.addAll(search(approvalStatus, locationSearch, startTime,
		    endTime, currentUser));
	}

	return resultList;
    }

    @Override
    public Tour getTour(long tourId) {
	return em.find(Tour.class, tourId);
    }

    @Override
    public void cancleTour(Tour tour) {
	tour.setStatus(TourStatus.CANCLED);
	tour.setUpdatedOn(new Date());
	em.persist(tour);
    }

    @Override
    public Location getLocation(long locationId) {
	return em.find(Location.class, locationId);
    }

    @Override
    public ApprovalStatus getApprovalStatus(int approvalStatusId) {
	return em.find(ApprovalStatus.class, approvalStatusId);
    }

    @Override
    public Capacity getCapacity(int capacityId) {
	return em.find(Capacity.class, capacityId);
    }

    @Override
    public PaymentCondition getPaymentCondition(int paymentConditionId) {
	return em.find(PaymentCondition.class, paymentConditionId);
    }

    @Override
    public DeliveryCondition getDeliveryCondition(int deliveryConditionId) {
	return em.find(DeliveryCondition.class, deliveryConditionId);
    }

}
