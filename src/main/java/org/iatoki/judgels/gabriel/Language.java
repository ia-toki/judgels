package org.iatoki.judgels.gabriel;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface Language {

    Set<String> getAllowedExtensions();

    List<String> getCompilationCommand(String sourceFilename);

    String getExecutableFilename(String sourceFilename);

    List<String> getExecutionCommand(String sourceFilename);
}
