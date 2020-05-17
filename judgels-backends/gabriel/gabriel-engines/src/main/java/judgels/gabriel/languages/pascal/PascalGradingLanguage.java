package judgels.gabriel.languages.pascal;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.GradingLanguage;
import org.apache.commons.io.FilenameUtils;

public class PascalGradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "Pascal";
    }

    @Override
    public List<String> getAllowedExtensions() {
        return ImmutableList.of("pas");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames) {
        return ImmutableList.of("/usr/bin/ppcx64", sourceFilename, "-O2", "-XS", "-Sg");
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
