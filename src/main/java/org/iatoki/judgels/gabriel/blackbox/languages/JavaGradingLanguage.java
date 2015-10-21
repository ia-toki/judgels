package org.iatoki.judgels.gabriel.blackbox.languages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FilenameUtils;
import org.iatoki.judgels.gabriel.AbstractGradingLanguage;

import java.util.List;
import java.util.Set;

public final class JavaGradingLanguage extends AbstractGradingLanguage {

    @Override
    public String getName() {
        return "Java 8";
    }

    @Override
    public Set<String> getAllowedExtensions() {
        return ImmutableSet.of("java");
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return FilenameUtils.removeExtension(sourceFilename) + ".class";
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename) {
        return ImmutableList.of("/usr/bin/javac", sourceFilename);
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        String executableClassname = FilenameUtils.removeExtension(sourceFilename);
        return ImmutableList.of("/usr/bin/java", executableClassname);
    }
}
