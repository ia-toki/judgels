package org.iatoki.judgels.gabriel.languages;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.List;

public final class CppLanguageProvider implements LanguageProvider {

    @Override
    public List<String> getCompilationCommand(File sourceFile) {
        return ImmutableList.of();
    }

    @Override
    public List<String> getExecutionCommand(File executableFile) {
        return ImmutableList.of();
    }
}
