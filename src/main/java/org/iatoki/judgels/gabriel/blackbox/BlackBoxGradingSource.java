package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingSource;

import java.util.Map;

public final class BlackBoxGradingSource implements GradingSource {
    private final Map<String, SourceFile> sourceFiles;

    public BlackBoxGradingSource(Map<String, SourceFile> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public Map<String, SourceFile> getSourceFiles() {
        return sourceFiles;
    }
}
