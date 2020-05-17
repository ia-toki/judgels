package judgels.gabriel.languages;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import judgels.gabriel.api.GradingLanguage;

public class OutputOnlyGradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "-";
    }

    @Override
    public Set<String> getAllowedExtensions() {
        return ImmutableSet.of("zip");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename, String... filenames) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        throw new UnsupportedOperationException();
    }
}
