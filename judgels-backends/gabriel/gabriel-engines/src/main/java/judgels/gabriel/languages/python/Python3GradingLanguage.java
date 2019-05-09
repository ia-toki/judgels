package judgels.gabriel.languages.python;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import judgels.gabriel.api.GradingLanguage;

public class Python3GradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "Python3";
    }

    @Override
    public Set<String> getAllowedExtensions() {
        return ImmutableSet.of("py");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename) {
        return ImmutableList.of("/bin/true");
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return sourceFilename;
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        return ImmutableList.of("/usr/bin/python3", sourceFilename);
    }
}
