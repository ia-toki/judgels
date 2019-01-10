package org.iatoki.judgels.gabriel;

public final class GradingRequest {

    private final String gradingJid;
    private final String problemJid;
    private final String gradingEngine;
    private final String gradingLanguage;
    private final SubmissionSource submissionSource;

    public GradingRequest(String gradingJid, String problemJid, String gradingEngine, String gradingLanguage, SubmissionSource submissionSource) {
        this.gradingJid = gradingJid;
        this.problemJid = problemJid;
        this.gradingEngine = gradingEngine;
        this.gradingLanguage = gradingLanguage;
        this.submissionSource = submissionSource;
    }

    public String getGradingJid() {
        return gradingJid;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public String getGradingEngine() {
        return gradingEngine;
    }

    public String getGradingLanguage() {
        return gradingLanguage;
    }

    public SubmissionSource getSubmissionSource() {
        return submissionSource;
    }
}
