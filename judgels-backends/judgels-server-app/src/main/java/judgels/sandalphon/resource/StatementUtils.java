package judgels.sandalphon.resource;

public class StatementUtils {
    private StatementUtils() {}

    public static String convertUnicodeToHtmlEntities(String s) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c >= 128) {
                result.append("&#").append((int) c).append(";");
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
