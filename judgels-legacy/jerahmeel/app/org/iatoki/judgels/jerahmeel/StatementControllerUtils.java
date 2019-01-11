package org.iatoki.judgels.jerahmeel;

import play.mvc.Controller;

public final class StatementControllerUtils {

    private StatementControllerUtils() {
        // prevent instantiation
    }

    public static void setCurrentStatementLanguage(String languageCode) {
        Controller.session("currentStatementLanguage", languageCode);
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
