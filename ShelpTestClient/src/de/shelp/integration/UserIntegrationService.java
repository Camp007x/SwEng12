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
 * 2015-05-21T13:49:17.273+02:00
 * Generated source version: 2.7.13
 * 
 */
@WebServiceClient(name = "UserIntegrationService", 
                  wsdlLocation = "http://localhost:8080/shelp/UserIntegration?wsdl",
                  targetNamespace = "http://integration.shelp.de/") 
public class UserIntegrationService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://integration.shelp.de/", "UserIntegrationService");
    public final static QName UserIntegrationPort = new QName("http://integration.shelp.de/", "UserIntegrationPort");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/shelp/UserIntegration?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(UserIntegrationService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://localhost:8080/shelp/UserIntegration?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public UserIntegrationService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public UserIntegrationService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public UserIntegrationService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public UserIntegrationService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public UserIntegrationService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public UserIntegrationService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns UserIntegration
     */
    @WebEndpoint(name = "UserIntegrationPort")
    public UserIntegration getUserIntegrationPort() {
        return super.getPort(UserIntegrationPort, UserIntegration.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns UserIntegration
     */
    @WebEndpoint(name = "UserIntegrationPort")
    public UserIntegration getUserIntegrationPort(WebServiceFeature... features) {
        return super.getPort(UserIntegrationPort, UserIntegration.class, features);
    }

}
