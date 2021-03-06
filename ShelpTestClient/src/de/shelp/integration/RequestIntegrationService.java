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
 * 2015-06-18T08:15:10.938+02:00
 * Generated source version: 2.7.13
 * 
 */
@WebServiceClient(name = "RequestIntegrationService", 
                  wsdlLocation = "http://localhost:8080/shelp/RequestIntegration?wsdl",
                  targetNamespace = "http://integration.shelp.de/") 
public class RequestIntegrationService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://integration.shelp.de/", "RequestIntegrationService");
    public final static QName RequestIntegrationPort = new QName("http://integration.shelp.de/", "RequestIntegrationPort");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/shelp/RequestIntegration?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(RequestIntegrationService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://localhost:8080/shelp/RequestIntegration?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public RequestIntegrationService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public RequestIntegrationService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RequestIntegrationService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public RequestIntegrationService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public RequestIntegrationService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public RequestIntegrationService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns RequestIntegration
     */
    @WebEndpoint(name = "RequestIntegrationPort")
    public RequestIntegration getRequestIntegrationPort() {
        return super.getPort(RequestIntegrationPort, RequestIntegration.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RequestIntegration
     */
    @WebEndpoint(name = "RequestIntegrationPort")
    public RequestIntegration getRequestIntegrationPort(WebServiceFeature... features) {
        return super.getPort(RequestIntegrationPort, RequestIntegration.class, features);
    }

}
