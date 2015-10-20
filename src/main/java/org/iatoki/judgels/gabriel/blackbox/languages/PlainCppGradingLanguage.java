package org.iatoki.judgels.gabriel.blackbox.languages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FilenameUtils;
import org.iatoki.judgels.gabriel.AbstractGradingLanguage;

import java.util.List;
import java.util.Set;

public final class PlainCppGradingLanguage extends AbstractGradingLanguage {

    @Override
    public String getName() {
        return "C++";
    }

    @Override
    public Set<String> getAllowedExtensions() {
        return ImmutableSet.of("cpp", "cc");
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return FilenameUtils.removeExtension(sourceFilename);
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename) {
        String executableFilename = getExecutableFilename(sourceFilename);
        return ImmutableList.of("/usr/bin/g++", "-o", executableFilename, sourceFilename);
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        String executableFilename = getExecutableFilename(sourceFilename);
        return ImmutableList.of("./" + executableFilename);
    }
}
