package org.iatoki.judgels.api.sandalphon;

public final class SandalphonLessonStatementRenderRequestParam {

    private String lessonSecret;
    private long currentMillis;
    private String statementLanguage;
    private String switchStatementLanguageUrl;

    public String getLessonSecret() {
        return lessonSecret;
    }

    public SandalphonLessonStatementRenderRequestParam setLessonSecret(String lessonSecret) {
        this.lessonSecret = lessonSecret;
        return this;
    }

    public long getCurrentMillis() {
        return currentMillis;
    }

    public SandalphonLessonStatementRenderRequestParam setCurrentMillis(long currentMillis) {
        this.currentMillis = currentMillis;
        return this;
    }

    public String getStatementLanguage() {
        return statementLanguage;
    }

    public SandalphonLessonStatementRenderRequestParam setStatementLanguage(String statementLanguage) {
        this.statementLanguage = statementLanguage;
        return this;
    }

    public String getSwitchStatementLanguageUrl() {
        return switchStatementLanguageUrl;
    }

    public SandalphonLessonStatementRenderRequestParam setSwitchStatementLanguageUrl(String switchStatementLanguageUrl) {
        this.switchStatementLanguageUrl = switchStatementLanguageUrl;
        return this;
    }
}
