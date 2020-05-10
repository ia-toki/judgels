package org.iatoki.judgels.sandalphon.lesson.statement;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class LessonStatementUtils {

    private static final Map<String, String> DEFAULT_TEXTS = ImmutableMap.of(
            "id-ID",
                    "<h3>Deskripsi</h3>\n",
            "en-US",
                    "<h3>Description</h3>\n"
    );

    private LessonStatementUtils() {
        // prevent instantiation
    }

    public static String getDefaultText(String languageCode) {
        if (DEFAULT_TEXTS.containsKey(languageCode)) {
            return DEFAULT_TEXTS.get(languageCode);
        } else {
            return DEFAULT_TEXTS.get("en-US");
        }
    }
}
