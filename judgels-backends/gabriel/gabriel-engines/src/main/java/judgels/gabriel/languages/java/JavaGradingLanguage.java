package judgels.gabriel.languages.java;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import judgels.gabriel.api.GradingLanguage;
import org.apache.commons.io.FilenameUtils;

public class JavaGradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "Java 8";
    }

    @Override
    public List<String> getAllowedExtensions() {
        return ImmutableList.of("java");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames) {
        String executableFilename = getExecutableFilename(sourceFilename);
        String mainClassName = FilenameUtils.removeExtension(sourceFilename);

        String javacCommand = String.join(" ", new ImmutableList.Builder<String>()
                .add("/usr/bin/javac", quote(sourceFilename))
                .addAll(Lists.transform(Arrays.asList(sourceFilenames), this::quote))
                .build());

        String jarCommand = String.join(" ", new ImmutableList.Builder<String>()
                .add("/usr/bin/jar", "cfe", quote(executableFilename), quote(mainClassName), "*.class")
                .build());

        return ImmutableList.of("/bin/bash", "-c", String.format("%s && %s", javacCommand, jarCommand));
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return FilenameUtils.removeExtension(sourceFilename) + ".jar";
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        return ImmutableList.of("/usr/bin/java", "-Xmx512M", "-Xss64M", "-jar", getExecutableFilename(sourceFilename));
    }

    private String quote(String filename) {
        return "\"" + filename + "\"";
    }
}
