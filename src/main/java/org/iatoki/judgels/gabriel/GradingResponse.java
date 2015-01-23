package org.iatoki.judgels.gabriel;

public interface GradingResponse {
    String getSubmissionJid();

    GradingResult getResult();
}
