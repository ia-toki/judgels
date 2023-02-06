package judgels.sandalphon.resource;

public class LanguageUtils {
    private LanguageUtils() {}

    public static String simplifyLanguageCode(String code) {
        if (code.startsWith("zh")) {
            return code;
        }
        String[] tokens = code.split("-");
        if (tokens.length < 2) {
            return code;
        }
        return tokens[0];
    }
}
