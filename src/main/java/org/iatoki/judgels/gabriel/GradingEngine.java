package org.iatoki.judgels.gabriel;

import org.iatoki.judgels.gabriel.sandboxes.SandboxFactory;

import java.io.File;

public interface GradingEngine {

    String getName();

    GradingConfig createDefaultGradingConfig();

    GradingConfig createGradingConfigFromJson(String json);

    GradingResult grade(File gradingDir, GradingConfig config, GradingLanguage language, GradingSource source, SandboxFactory sandboxFactory) throws GradingException;
}
