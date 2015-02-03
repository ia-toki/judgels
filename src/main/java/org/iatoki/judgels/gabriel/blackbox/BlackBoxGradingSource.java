package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingSource;

import java.util.Map;

public final class BlackBoxGradingSource implements GradingSource {
    private final String gradingLanguage;
    private final Map<String, SourceFile> sourceFiles;

    public BlackBoxGradingSource(String gradingLanguage, Map<String, SourceFile> sourceFiles) {
        this.gradingLanguage = gradingLanguage;
        this.sourceFiles = sourceFiles;
    }

    public String getGradingLanguage() {
        return gradingLanguage;
    }

    public Map<String, SourceFile> getSourceFiles() {
        return sourceFiles;
    }
}
