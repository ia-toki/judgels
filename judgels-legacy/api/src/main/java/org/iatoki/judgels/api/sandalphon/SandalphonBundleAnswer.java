package org.iatoki.judgels.api.sandalphon;

import java.util.Map;

public final class SandalphonBundleAnswer {

    private final Map<String, String> answers;
    private final String languageCode;

    public SandalphonBundleAnswer(Map<String, String> answers, String languageCode) {
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
