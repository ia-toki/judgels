package judgels.gabriel.languages.cpp;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

public class Cpp17GradingLanguage implements CppFamilyGradingLanguage {
    @Override
    public String getName() {
        return "C++17";
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public List<String> getAllowedExtensions() {
        return ImmutableList.of("cpp", "cc");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames) {
        return new ImmutableList.Builder<String>()
                .add("/usr/bin/g++", "-std=c++17", "-o", getExecutableFilename(sourceFilename))
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
