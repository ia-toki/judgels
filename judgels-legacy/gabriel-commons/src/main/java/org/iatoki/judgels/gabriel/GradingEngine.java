package org.iatoki.judgels.gabriel;

import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.GradingSource;
import judgels.gabriel.api.SandboxFactory;

import java.io.File;
import java.io.IOException;

public interface GradingEngine {

    String getName();

    GradingConfig createDefaultGradingConfig();

    GradingConfig createGradingConfigFromJson(String json) throws IOException;

    GradingResult grade(File gradingDir, GradingConfig config, GradingLanguage language, GradingSource source, SandboxFactory sandboxFactory)
            throws GradingException;
}
