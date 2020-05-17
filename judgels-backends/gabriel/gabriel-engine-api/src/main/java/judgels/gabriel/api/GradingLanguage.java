package judgels.gabriel.api;

import java.util.List;
import java.util.Set;

public interface GradingLanguage {
    String getName();
    Set<String> getAllowedExtensions();
    List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames);
    String getExecutableFilename(String sourceFilename);
    List<String> getExecutionCommand(String sourceFilename);
}
