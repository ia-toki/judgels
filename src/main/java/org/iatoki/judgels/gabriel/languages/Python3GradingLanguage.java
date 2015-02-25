package org.iatoki.judgels.gabriel.languages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.iatoki.judgels.gabriel.AbstractGradingLanguage;

import java.util.List;
import java.util.Set;

public final class Python3GradingLanguage extends AbstractGradingLanguage {
    @Override
    public String getName() {
        return "Python 3";
    }

    @Override
    public Set<String> getAllowedExtensions() {
        return ImmutableSet.of("py");
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return sourceFilename;
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename) {
        return ImmutableList.of("/usr/bin/true");
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        return ImmutableList.of("/usr/bin/python3", sourceFilename);
    }
}