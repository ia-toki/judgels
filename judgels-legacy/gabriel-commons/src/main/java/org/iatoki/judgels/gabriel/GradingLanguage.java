package org.iatoki.judgels.gabriel;

import java.util.List;

public interface GradingLanguage {

    String getName();

    List<String> getCompilationCommand(String sourceFilename);

    String getExecutableFilename(String sourceFilename);

    List<String> getExecutionCommand(String sourceFilename);

    String verifyFile(String filename, byte[] content);
}
