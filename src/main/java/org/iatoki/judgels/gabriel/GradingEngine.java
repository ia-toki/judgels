package org.iatoki.judgels.gabriel;

public interface GradingEngine {

    String getName();

    GradingConfig createDefaultGradingConfig();

    GradingConfig createGradingConfigFromJson(String json);

    void cleanUp();
}
