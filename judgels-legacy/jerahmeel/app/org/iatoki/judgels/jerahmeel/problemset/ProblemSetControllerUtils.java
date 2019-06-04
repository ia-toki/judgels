package org.iatoki.judgels.jerahmeel.problemset;

import play.mvc.Controller;

public final class ProblemSetControllerUtils {

    private ProblemSetControllerUtils() {
        // prevent instantiation
    }

    public static String getCurrentStatementLanguage() {
        String lang = Controller.session("currentStatementLanguage");
        if (lang == null) {
            return "en-US";
        } else {
            return lang;
        }
    }
}
