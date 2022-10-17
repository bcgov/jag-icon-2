package ca.bc.gov.open.icon.models.serializers;

public final class BooleanConverter {
    private BooleanConverter() {}

    public static String print(boolean b) {
        return b ? "1" : "0";
    }

    public static boolean parse(String s) {
        if (s != null) return s.equals("1");
        else return false;
    }
}
