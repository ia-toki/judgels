package org.iatoki.judgels.gabriel.blackbox.languages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.iatoki.judgels.gabriel.AbstractGradingLanguage;

import java.util.List;
import java.util.Set;

public final class PascalGradingLanguage extends AbstractGradingLanguage {

    @Override
    public String getName() {
        return "Pascal";
    }

    @Override
    public Set<String> getAllowedExtensions() {
        return ImmutableSet.of("pas");
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return sourceFilename.substring(0, sourceFilename.lastIndexOf('.'));
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename) {
        String executableFilename = getExecutableFilename(sourceFilename);
        return ImmutableList.of("/usr/bin/fpc", sourceFilename, "-O2", "-XS", "-Sg");
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        String executableFilename = getExecutableFilename(sourceFilename);
        return ImmutableList.of("./" + executableFilename);
    }
}
