package ca.bc.gov.open.icon.models.serializers;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanSoapConverterAdapter extends XmlAdapter<String, Boolean> {

    public Boolean unmarshal(String value) {
        return (ca.bc.gov.open.icon.models.serializers.BooleanSoapConverter.parse(value));
    }

    public String marshal(Boolean value) {
        return (ca.bc.gov.open.icon.models.serializers.BooleanSoapConverter.print(value));
    }
}
