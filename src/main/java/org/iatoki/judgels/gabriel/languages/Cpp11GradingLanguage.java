package org.iatoki.judgels.gabriel.languages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.iatoki.judgels.gabriel.AbstractGradingLanguage;

import java.util.List;
import java.util.Set;

public final class Cpp11GradingLanguage extends AbstractGradingLanguage {
    @Override
    public String getName() {
        return "C++11";
    }

    @Override
    public Set<String> getAllowedExtensions() {
        return ImmutableSet.of("cpp", "cc");
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return sourceFilename.substring(0, sourceFilename.lastIndexOf('.'));
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename) {
        String executableFilename = getExecutableFilename(sourceFilename);
        return ImmutableList.of("/usr/bin/g++", "-std=c++11", "-o", executableFilename, sourceFilename, "-s", "-static", "-lm");
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        String executableFilename = getExecutableFilename(sourceFilename);
        return ImmutableList.of("./" + executableFilename);
    }
}
