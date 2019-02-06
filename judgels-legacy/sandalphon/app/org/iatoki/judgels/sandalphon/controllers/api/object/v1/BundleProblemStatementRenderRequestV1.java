package org.iatoki.judgels.sandalphon.controllers.api.object.v1;

public final class BundleProblemStatementRenderRequestV1 {

    public String problemJid;
    public String clientJid;
    public int totpCode;
    public String statementLanguage;
    public String switchStatementLanguageUrl;
    public String postSubmitUrl;
    public String reasonNotAllowedToSubmit;

    public String getProblemJid() {
        return problemJid;
    }

    public void setProblemJid(String problemJid) {
        this.problemJid = problemJid;
    }

    public String getClientJid() {
        return clientJid;
    }

    public void setClientJid(String clientJid) {
        this.clientJid = clientJid;
    }

    public int getTotpCode() {
        return totpCode;
    }

    public void setTotpCode(int totpCode) {
        this.totpCode = totpCode;
    }

    public String getStatementLanguage() {
        return statementLanguage;
    }

    public void setStatementLanguage(String statementLanguage) {
        this.statementLanguage = statementLanguage;
    }

    public String getSwitchStatementLanguageUrl() {
        return switchStatementLanguageUrl;
    }

    public void setSwitchStatementLanguageUrl(String switchStatementLanguageUrl) {
        this.switchStatementLanguageUrl = switchStatementLanguageUrl;
    }

    public String getPostSubmitUrl() {
        return postSubmitUrl;
    }

    public void setPostSubmitUrl(String postSubmitUrl) {
        this.postSubmitUrl = postSubmitUrl;
    }

    public String getReasonNotAllowedToSubmit() {
        return reasonNotAllowedToSubmit;
    }

    public void setReasonNotAllowedToSubmit(String reasonNotAllowedToSubmit) {
        this.reasonNotAllowedToSubmit = reasonNotAllowedToSubmit;
    }
}
