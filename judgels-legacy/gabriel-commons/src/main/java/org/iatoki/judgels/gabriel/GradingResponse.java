package org.iatoki.judgels.gabriel;

public final class GradingResponse {

    private String gradingJid;
    private GradingResult result;

    public GradingResponse(String gradingJid, GradingResult result) {
        this.gradingJid = gradingJid;
        this.result = result;
    }

    public String getGradingJid() {
        return gradingJid;
    }

    public GradingResult getResult() {
        return result;
    }
}
