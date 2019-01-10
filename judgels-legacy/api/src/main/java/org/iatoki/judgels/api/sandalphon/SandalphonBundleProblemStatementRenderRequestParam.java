package org.iatoki.judgels.api.sandalphon;

public final class SandalphonBundleProblemStatementRenderRequestParam {

    private String problemSecret;
    private long currentMillis;
    private String statementLanguage;
    private String switchStatementLanguageUrl;
    private String postSubmitUrl;
    private String reasonNotAllowedToSubmit;

    public String getProblemSecret() {
        return problemSecret;
    }

    public SandalphonBundleProblemStatementRenderRequestParam setProblemSecret(String problemSecret) {
        this.problemSecret = problemSecret;
        return this;
    }

    public long getCurrentMillis() {
        return currentMillis;
    }

    public SandalphonBundleProblemStatementRenderRequestParam setCurrentMillis(long currentMillis) {
        this.currentMillis = currentMillis;
        return this;
    }

    public String getStatementLanguage() {
        return statementLanguage;
    }

    public SandalphonBundleProblemStatementRenderRequestParam setStatementLanguage(String statementLanguage) {
        this.statementLanguage = statementLanguage;
        return this;
    }

    public String getSwitchStatementLanguageUrl() {
        return switchStatementLanguageUrl;
    }

    public SandalphonBundleProblemStatementRenderRequestParam setSwitchStatementLanguageUrl(String switchStatementLanguageUrl) {
        this.switchStatementLanguageUrl = switchStatementLanguageUrl;
        return this;
    }

    public String getPostSubmitUrl() {
        return postSubmitUrl;
    }

    public SandalphonBundleProblemStatementRenderRequestParam setPostSubmitUrl(String postSubmitUrl) {
        this.postSubmitUrl = postSubmitUrl;
        return this;
    }

    public String getReasonNotAllowedToSubmit() {
        return reasonNotAllowedToSubmit;
    }

    public SandalphonBundleProblemStatementRenderRequestParam setReasonNotAllowedToSubmit(String reasonNotAllowedToSubmit) {
        this.reasonNotAllowedToSubmit = reasonNotAllowedToSubmit;
        return this;
    }
}
