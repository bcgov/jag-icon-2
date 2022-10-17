package ca.bc.gov.open.icon.models.serializers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BooleanConverter {
    private BooleanConverter() {}

    public static String print(boolean b) {
        return b ? "1" : "0";
    }

    public static boolean parse(String s) {
        try {
            return s.equals("1");
        } catch (Exception ex) {
            log.warn("Bad date received from soap request - invalid boolean: " + s);
            return false;
        }
    }
}
