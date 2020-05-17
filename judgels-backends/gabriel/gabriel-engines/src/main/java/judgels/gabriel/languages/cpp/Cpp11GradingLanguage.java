package judgels.gabriel.languages.cpp;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;

public class Cpp11GradingLanguage implements CppFamilyGradingLanguage {
    @Override
    public String getName() {
        return "C++11";
    }

    @Override
    public Set<String> getAllowedExtensions() {
        return ImmutableSet.of("cpp", "cc");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames) {
        return new ImmutableList.Builder<String>()
                .add("/usr/bin/g++", "-std=c++11", "-o", getExecutableFilename(sourceFilename))
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
