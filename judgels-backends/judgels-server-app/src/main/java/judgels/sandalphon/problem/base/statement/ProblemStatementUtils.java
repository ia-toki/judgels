package judgels.sandalphon.problem.base.statement;

import java.util.Map;

public final class ProblemStatementUtils {

    private static final Map<String, String> DEFAULT_TITLES = Map.of(
            "id-ID", "Kotak Apel",
            "en-US", "Boxes of Apples"
    );

    private ProblemStatementUtils() {}

    public static String getDefaultTitle(String language) {
        if (DEFAULT_TITLES.containsKey(language)) {
            return DEFAULT_TITLES.get(language);
        } else {
            return DEFAULT_TITLES.get("en-US");
        }
    }
}
