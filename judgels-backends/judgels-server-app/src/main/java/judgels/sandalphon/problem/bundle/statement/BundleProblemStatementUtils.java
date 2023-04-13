package judgels.sandalphon.problem.bundle.statement;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public final class BundleProblemStatementUtils {
    private static final Map<String, String> DEFAULT_STATEMENTS = ImmutableMap.of(
            "id-ID",
                    "(instruksi soal)",
            "en-US",
                    "(problem instructions)"
    );

    private BundleProblemStatementUtils() {
        // prevent instantiation
    }

    public static String getDefaultStatement(String languageCode) {
        if (DEFAULT_STATEMENTS.containsKey(languageCode)) {
            return DEFAULT_STATEMENTS.get(languageCode);
        } else {
            return DEFAULT_STATEMENTS.get("en-US");
        }
    }
}
