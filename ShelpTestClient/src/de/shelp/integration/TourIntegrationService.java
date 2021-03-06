package de.shelp.integration;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.7.13
 * 2015-06-18T08:15:43.778+02:00
 * Generated source version: 2.7.13
 * 
 */
@WebServiceClient(name = "TourIntegrationService", 
                  wsdlLocation = "http://localhost:8080/shelp/TourIntegration?wsdl",
                  targetNamespace = "http://integration.shelp.de/") 
public class TourIntegrationService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://integration.shelp.de/", "TourIntegrationService");
    public final static QName TourIntegrationPort = new QName("http://integration.shelp.de/", "TourIntegrationPort");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/shelp/TourIntegration?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(TourIntegrationService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://localhost:8080/shelp/TourIntegration?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public TourIntegrationService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public TourIntegrationService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public TourIntegrationService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public TourIntegrationService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public TourIntegrationService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public TourIntegrationService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns TourIntegration
     */
    @WebEndpoint(name = "TourIntegrationPort")
    public TourIntegration getTourIntegrationPort() {
        return super.getPort(TourIntegrationPort, TourIntegration.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns TourIntegration
     */
    @WebEndpoint(name = "TourIntegrationPort")
    public TourIntegration getTourIntegrationPort(WebServiceFeature... features) {
        return super.getPort(TourIntegrationPort, TourIntegration.class, features);
    }

}
