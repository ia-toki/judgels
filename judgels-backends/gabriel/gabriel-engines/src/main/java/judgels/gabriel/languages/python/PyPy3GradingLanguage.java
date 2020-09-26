package judgels.gabriel.languages.python;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.GradingLanguage;

public class PyPy3GradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "PyPy 3";
    }

    @Override
    public List<String> getAllowedExtensions() {
        return ImmutableList.of("py");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames) {
        return ImmutableList.of("/bin/true");
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return sourceFilename;
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        return ImmutableList.of("/usr/bin/pypy3", sourceFilename);
    }
}
