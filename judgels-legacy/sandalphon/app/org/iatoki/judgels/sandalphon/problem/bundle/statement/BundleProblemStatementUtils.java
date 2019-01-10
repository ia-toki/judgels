package org.iatoki.judgels.sandalphon.problem.bundle.statement;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class BundleProblemStatementUtils {

    // TODO refactor this
    private static final Map<String, String> DEFAULT_STATEMENTS = ImmutableMap.of(
            "id-ID",
                    "<h3>Bundel Soal</h3>\n",
            "en-US",
                    "<h3>Problem Bundle</h3>\n"
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
