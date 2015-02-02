package org.iatoki.judgels.gabriel;

public interface GradingRequest {
    String getSubmissionJid();

    String getProblemJid();

    long getProblemLastUpdate();

    String getGradingType();

    GradingSource getGradingSource();
}
