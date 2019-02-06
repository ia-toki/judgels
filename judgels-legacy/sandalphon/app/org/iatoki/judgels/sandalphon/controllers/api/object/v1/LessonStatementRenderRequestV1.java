package org.iatoki.judgels.sandalphon.controllers.api.object.v1;

public final class LessonStatementRenderRequestV1 {

    public String lessonJid;
    public String clientJid;
    public int totpCode;
    public String statementLanguage;
    public String switchStatementLanguageUrl;

    public String getLessonJid() {
        return lessonJid;
    }

    public void setLessonJid(String lessonJid) {
        this.lessonJid = lessonJid;
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
}
