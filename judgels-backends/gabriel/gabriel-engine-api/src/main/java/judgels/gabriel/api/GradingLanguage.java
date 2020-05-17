package judgels.gabriel.api;

import java.util.List;

public interface GradingLanguage {
    String getName();
    List<String> getAllowedExtensions();
    List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames);
    String getExecutableFilename(String sourceFilename);
    List<String> getExecutionCommand(String sourceFilename);
}
