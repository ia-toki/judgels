package org.iatoki.judgels.gabriel;

public interface GradingRequest {
    String getGradingJid();

    String getProblemJid();

    long getGradingLastUpdateTime();

    String getGradingEngine();

    String getGradingLanguage();

    GradingSource getGradingSource();
}
