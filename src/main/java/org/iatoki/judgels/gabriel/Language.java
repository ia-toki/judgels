package org.iatoki.judgels.gabriel;

import java.util.List;
import java.util.Set;

public interface Language {

    String getName();

    Set<String> getAllowedExtensions();

    List<String> getCompilationCommand(String sourceFilename);

    String getExecutableFilename(String sourceFilename);

    List<String> getExecutionCommand(String sourceFilename);
}
