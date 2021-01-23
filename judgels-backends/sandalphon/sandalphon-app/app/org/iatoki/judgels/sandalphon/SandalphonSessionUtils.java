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

    public static String getJustCreatedProblemSlug(Http.Request req) {
        return req.session().getOptional("problemSlug").orElse(null);
    }

    public static String getJustCreatedProblemAdditionalNote(Http.Request req) {
        return req.session().getOptional("problemAdditionalNote").orElse(null);
    }

    public static String getJustCreatedProblemInitLanguage(Http.Request req) {
        return req.session().getOptional("initLanguageCode").orElse(null);
    }

    public static boolean wasProblemJustCreated(Http.Request req) {
        return req.session().getOptional("problemSlug").isPresent()
                && req.session().getOptional("problemAdditionalNote").isPresent()
                && req.session().getOptional("initLanguageCode").isPresent();
    }

    public static Map<String, String> newJustCreatedProblem(String slug, String additionalNote, String initLanguage) {
        return ImmutableMap.of(
                "problemSlug", slug,
                "problemAdditionalNote", additionalNote,
                "initLanguageCode", initLanguage);
    }

    public static String[] removeJustCreatedProblem() {
        return new String[]{"problemSlug", "problemAdditionalNote", "initLanguageCode"};
    }
}
