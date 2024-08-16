package judgels.gabriel.aggregators;

public class PointUtils {
    private PointUtils() {}

    public static String formatPoints(double pts) {
        String result = "" + pts;
        if (result.contains(".")) {
            while (result.charAt(result.length() - 1) == '0') {
                result = result.substring(0, result.length() - 1);
            }
            if (result.charAt(result.length() - 1) == '.') {
                result = result.substring(0, result.length() - 1);
            }
        }
        return result;
    }
}
