package org.iatoki.judgels.sandalphon.problem.base.statement;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class ProblemStatementUtils {

    private static final Map<String, String> DEFAULT_TITLES = ImmutableMap.of(
            "id-ID", "(judul soal)",
            "en-US", "(problem title)"
    );

    private ProblemStatementUtils() {
        // prevent instantiation
    }

    public static String getDefaultTitle(String languageCode) {
        if (DEFAULT_TITLES.containsKey(languageCode)) {
            return DEFAULT_TITLES.get(languageCode);
        } else {
            return DEFAULT_TITLES.get("en-US");
        }
    }
}
