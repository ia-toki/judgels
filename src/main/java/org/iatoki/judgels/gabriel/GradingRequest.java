package org.iatoki.judgels.gabriel;

import java.util.Map;

public interface GradingRequest {
    String getSubmissionJid();

    String getProblemJid();

    long getProblemLastUpdate();

    GradingType getGradingType();
}
