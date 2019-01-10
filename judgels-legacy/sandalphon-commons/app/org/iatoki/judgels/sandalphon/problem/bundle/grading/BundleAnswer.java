package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import java.util.Map;

public final class BundleAnswer {

    private final Map<String, String> answers;
    private final String languageCode;

    public BundleAnswer(Map<String, String> answers, String languageCode) {
        this.answers = answers;
        this.languageCode = languageCode;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public String getLanguageCode() {
        return languageCode;
    }
}
