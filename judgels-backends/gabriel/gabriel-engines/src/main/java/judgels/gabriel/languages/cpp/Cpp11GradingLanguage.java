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
    public List<String> getCompilationCommand(String sourceFilename) {
        String executableFilename = getExecutableFilename(sourceFilename);
        return ImmutableList.of("/usr/bin/g++", "-std=c++11", "-o", executableFilename, sourceFilename, "-O2", "-lm");
    }

    @Override
    public List<String> getCompilationOnlyCommand(String sourceFilename, String objectFilename) {
        return ImmutableList.of("/usr/bin/g++", "-std=c++11", "-o", objectFilename, "-c", sourceFilename, "-O2");
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
