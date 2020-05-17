package judgels.gabriel.languages;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.GradingLanguage;

public class OutputOnlyGradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "-";
    }

    @Override
    public List<String> getAllowedExtensions() {
        return ImmutableList.of("zip");
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
