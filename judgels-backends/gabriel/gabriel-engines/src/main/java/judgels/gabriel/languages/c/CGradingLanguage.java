package judgels.gabriel.languages.c;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.GradingLanguage;
import org.apache.commons.io.FilenameUtils;

public class CGradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "C";
    }

    @Override
    public List<String> getAllowedExtensions() {
        return ImmutableList.of("c");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames) {
        return new ImmutableList.Builder<String>()
                .add("/usr/bin/gcc", "-std=gnu99", "-o", getExecutableFilename(sourceFilename))
                .add(sourceFilename)
                .add(sourceFilenames)
                .add("-O2", "-lm")
                .build();
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
