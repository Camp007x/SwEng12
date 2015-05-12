package de.shelp.integration;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.13
 * 2015-05-11T21:51:48.006+02:00
 * Generated source version: 2.7.13
 * 
 */
@WebService(targetNamespace = "http://integration.shelp.de/", name = "StateIntegration")
@XmlSeeAlso({ObjectFactory.class})
public interface StateIntegration {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getApprovalStatus", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetApprovalStatus")
    @WebMethod
    @ResponseWrapper(localName = "getApprovalStatusResponse", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetApprovalStatusResponse")
    public de.shelp.integration.ApprovalStatusResponse getApprovalStatus();

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getLocations", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetLocations")
    @WebMethod
    @ResponseWrapper(localName = "getLocationsResponse", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetLocationsResponse")
    public de.shelp.integration.LocationResponse getLocations();

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getAllLists", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetAllLists")
    @WebMethod
    @ResponseWrapper(localName = "getAllListsResponse", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetAllListsResponse")
    public de.shelp.integration.AllListResponse getAllLists();

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getDeliveryConditions", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetDeliveryConditions")
    @WebMethod
    @ResponseWrapper(localName = "getDeliveryConditionsResponse", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetDeliveryConditionsResponse")
    public de.shelp.integration.DeliveryConditionResponse getDeliveryConditions();

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getPaymentConditions", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetPaymentConditions")
    @WebMethod
    @ResponseWrapper(localName = "getPaymentConditionsResponse", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetPaymentConditionsResponse")
    public de.shelp.integration.PaymentConditionsResponse getPaymentConditions();

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getCapacities", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetCapacities")
    @WebMethod
    @ResponseWrapper(localName = "getCapacitiesResponse", targetNamespace = "http://integration.shelp.de/", className = "de.shelp.integration.GetCapacitiesResponse")
    public de.shelp.integration.CapacitiesResponse getCapacities();
}
