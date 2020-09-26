package judgels.gabriel.languages.go;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.GradingLanguage;
import org.apache.commons.io.FilenameUtils;

public class GoGradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "Go";
    }

    @Override
    public List<String> getAllowedExtensions() {
        return ImmutableList.of("go");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames) {
        return ImmutableList.of("/usr/bin/go", "build", sourceFilename);
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return FilenameUtils.removeExtension(sourceFilename);
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        String executableFilename = getExecutableFilename(sourceFilename);
        return ImmutableList.of("./" + executableFilename);
    }
}
