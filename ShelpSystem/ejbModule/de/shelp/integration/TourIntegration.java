package de.shelp.integration;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.jboss.logging.Logger;
import org.jboss.ws.api.annotation.WebContext;

import de.shelp.dao.local.ShelpTourDAOLocal;
import de.shelp.dto.ReturnCodeResponse;
import de.shelp.dto.tour.TourTO;
import de.shelp.enums.ReturnCode;
import de.shelp.exception.ShelpException;
import de.shelp.exception.TourNotValidException;
import de.shelp.util.TourDtoAssembler;

@WebService
@WebContext(contextRoot = "/shelp")
@Stateless
public class TourIntegration {

    private static final Logger LOGGER = Logger.getLogger(TourIntegration.class);

    /**
     * EJB zur Abfrage von Datens�tzen Referenz auf die EJB wird per Dependency
     * Injection gef�llt.
     */
    @EJB(beanName = "ShelpTourDAO", beanInterface = ShelpTourDAOLocal.class)
    private ShelpTourDAOLocal dao;

    /**
     * EJB zur Erzeugung von DataTransferObjects
     */
    @EJB
    private TourDtoAssembler dtoAssembler;

    public ReturnCodeResponse createTour(TourTO tourTO, String sessionId) {
	ReturnCodeResponse response = new ReturnCodeResponse();
	try {
	    if (!tourTO.isValid()) {
		LOGGER.warn("Es sind nicht alle Felder der Fahrt gef�llt.");
		throw new TourNotValidException(ReturnCode.ERROR, "Es sind nicht alle Felder der Fahrt gef�llt.");
	    } else {

	    }
	} catch (ShelpException ex) {
	    response.setReturnCode(ex.getErrorCode());
	    response.setMessage(ex.getMessage());
	}

	return response;

    }
}
