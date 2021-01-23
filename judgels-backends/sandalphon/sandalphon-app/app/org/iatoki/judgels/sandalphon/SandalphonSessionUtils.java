package org.iatoki.judgels.sandalphon;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import play.mvc.Http;

public class SandalphonSessionUtils {
    private SandalphonSessionUtils() {}

    public static String getCurrentStatementLanguage(Http.Request req) {
        return req.session().getOptional("currentStatementLanguage").orElse(null);
    }

    public static Map<String, String> newCurrentStatementLanguage(String value) {
        return ImmutableMap.of("currentStatementLanguage", value);
    }
}
