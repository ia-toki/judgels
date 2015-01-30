package org.iatoki.judgels.gabriel;

public interface Grader {

    String getName();

    GradingConfig createDefaultGradingConfig();

    GradingConfig createGradingConfigFromJson(String json);

    void cleanUp();
}
