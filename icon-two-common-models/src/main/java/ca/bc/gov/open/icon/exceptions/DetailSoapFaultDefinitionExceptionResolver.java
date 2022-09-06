package ca.bc.gov.open.icon.exceptions;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

public class DetailSoapFaultDefinitionExceptionResolver extends SoapFaultMappingExceptionResolver {

    private static final QName CODE = new QName("code");
    private static final QName DESCRIPTION = new QName("description");

    //    private static final ObjectFactory FACTORY = new ObjectFactory();
    //    private final Marshaller marshaller;

    //    public DetailSoapFaultDefinitionExceptionResolver() throws JAXBException {
    //        JAXBContext jaxbContext = JAXBContext.newInstance("your.schema");
    //        this.marshaller = jaxbContext.createMarshaller();
    //    }

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        logger.warn("Exception processed ", ex);

        // code for adding fault details

        SoapFaultDefinition soapFaultDefinition = new SoapFaultDefinition();
        String ENVELOPE_NAMESPACE_URI = "http://schemas.xmlsoap.org/soap/envelope/";

        //      soapFaultDefinition.setFaultStringOrReason("--" + ex);

        //      soapFaultDefinition.setLocale(Locale.ENGLISH);

        QName CLIENT_FAULT_NAME = new QName(ENVELOPE_NAMESPACE_URI, "5003", "e");
        soapFaultDefinition.setFaultCode(CLIENT_FAULT_NAME);
        setDefaultFault(soapFaultDefinition);
        Result result = fault.addFaultDetail().getResult();

        // marshal
        try {
            ServiceFault serviceFault = ((ServiceFaultException) ex).getServiceFault();
            JAXBContext.newInstance(ServiceFault.class)
                    .createMarshaller()
                    .marshal(serviceFault, result);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
