package org.iatoki.judgels.gabriel;

public final class GradingResponse {
    private final String submissionJid;
    private final GradingResult result;

    public GradingResponse(String submissionJid, GradingResult result) {
        this.submissionJid = submissionJid;
        this.result = result;
    }

    public String getSubmissionJid() {
        return submissionJid;
    }

    public GradingResult getResult() {
        return result;
    }
}
