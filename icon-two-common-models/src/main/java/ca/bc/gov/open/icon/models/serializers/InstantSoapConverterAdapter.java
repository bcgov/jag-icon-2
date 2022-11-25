package ca.bc.gov.open.icon.models.serializers;

import java.time.Instant;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class InstantSoapConverterAdapter extends XmlAdapter<String, Instant> {
    public Instant unmarshal(String value) {
        return (ca.bc.gov.open.icon.models.serializers.InstantSoapConverter.parse(value));
    }

    public String marshal(Instant value) {
        return (ca.bc.gov.open.icon.models.serializers.InstantSoapConverter.print(value));
    }
}
