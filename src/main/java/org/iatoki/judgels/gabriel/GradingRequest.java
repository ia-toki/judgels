package org.iatoki.judgels.gabriel;

public interface GradingRequest {
    String getGradingJid();

    String getProblemJid();

    String getGradingEngine();

    String getGradingLanguage();

    GradingSource getGradingSource();
}
