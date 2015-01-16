package org.iatoki.judgels.gabriel;

import java.io.File;
import java.util.List;

public interface Language {

    String getName();

    List<String> getCompilationCommand(String sourceFilename);

    String getExecutableFilename(String sourceFilename);

    List<String> getExecutionCommand(String sourceFilename);
}
