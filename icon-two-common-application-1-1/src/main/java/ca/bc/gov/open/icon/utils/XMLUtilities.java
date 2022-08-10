package ca.bc.gov.open.icon.utils;

import java.io.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public final class XMLUtilities {

    // remove the request XML (contain <![CDATA[ ... ]]> ) to recreate a request object
    public static <T, G> G convertReq(T req, G reqDoc, String service) {
        try {
            // marshall to T type
            JAXBContext reqContext = JAXBContext.newInstance(req.getClass());
            Marshaller reqMarshaller = reqContext.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            reqMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            reqMarshaller.marshal(req, stringWriter);

            String xml = stringWriter.toString();

            // the requst XML contains CDATA so the anchor tags within the CDATA become '&lt;' and
            // '&gt;' instead of '<' and '>'.
            // In here,  we convert this XML to only contain '<' and '>' tags.
            xml =
                    xml.replaceAll("\\&lt;", "<")
                            .replaceAll("\\&gt;", ">")
                            .replaceAll(service, service + "Document");

            // unmarshall to G Type and return the object
            JAXBContext reqDocContext = JAXBContext.newInstance(reqDoc.getClass());
            Unmarshaller reqDocMarshaller = reqDocContext.createUnmarshaller();

            return (G) reqDocMarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        } catch (JAXBException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // add <![CDATA[ ... ]]> ) to the responsed object's XML and recreate the responsed object
    public static <T, G> G convertResp(T respDoc, G resp, String service) {
        try {
            // marshall to T type
            JAXBContext respDocContext = JAXBContext.newInstance(respDoc.getClass());
            Marshaller respDocMarshaller = respDocContext.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            respDocMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            respDocMarshaller.marshal(respDoc, stringWriter);
            String xml = stringWriter.toString();

            // the requst XML contains XMLString and UserTokenString blocks which need to convert to
            // CDATA
            xml =
                    xml.replaceAll(service + "Document", service)
                            .replaceAll("&#13;", "\r")
                            .replaceAll(
                                    "<XMLString>", "<XMLString><![CDATA[<?xml version=\"1.0\"?>")
                            .replaceAll("</XMLString>", "]]></XMLString>")
                            .replaceAll("<UserTokenString>", "<UserTokenString><![CDATA[")
                            .replaceAll("</UserTokenString>", "]]></UserTokenString>");

            // unmarshall to G type and return object
            JAXBContext respContext = JAXBContext.newInstance(resp.getClass());
            Unmarshaller respMarshaller = respContext.createUnmarshaller();

            return (G) respMarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        } catch (JAXBException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
