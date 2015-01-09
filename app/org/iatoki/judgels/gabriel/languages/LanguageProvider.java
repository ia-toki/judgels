package org.iatoki.judgels.gabriel.languages;

import java.io.File;
import java.util.List;

public interface LanguageProvider {

    List<String> getCompilationCommand(File sourceFile);

    List<String> getExecutionCommand(File executableFile);
}
