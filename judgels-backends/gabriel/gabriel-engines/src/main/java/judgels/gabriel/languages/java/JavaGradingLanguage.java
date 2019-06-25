package judgels.gabriel.languages.java;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import judgels.gabriel.api.GradingLanguage;
import org.apache.commons.io.FilenameUtils;

public class JavaGradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "Java 8";
    }

    @Override
    public Set<String> getAllowedExtensions() {
        return ImmutableSet.of("java");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename) {
        String mainClassName = FilenameUtils.removeExtension(sourceFilename);
        return ImmutableList.of("/bin/bash", "-c", String.format("/usr/bin/javac %s ; /usr/bin/jar cfe %s %s *.class",
                sourceFilename,
                getExecutableFilename(sourceFilename),
                mainClassName));
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return FilenameUtils.removeExtension(sourceFilename) + ".jar";
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        return ImmutableList.of("/usr/bin/java", "-jar", getExecutableFilename(sourceFilename));
    }
}
