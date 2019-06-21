package judgels.gabriel.languages.pascal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import judgels.gabriel.api.GradingLanguage;
import org.apache.commons.io.FilenameUtils;

public class PascalGradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "Pascal";
    }

    @Override
    public Set<String> getAllowedExtensions() {
        return ImmutableSet.of("pas");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename) {
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
