package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.GradingSource;

import java.util.Map;

public final class BlackBoxGradingSource implements GradingSource {
    private final GradingLanguage gradingLanguage;
    private final Map<String, SourceFile> sourceFiles;

    public BlackBoxGradingSource(GradingLanguage gradingLanguage, Map<String, SourceFile> sourceFiles) {
        this.gradingLanguage = gradingLanguage;
        this.sourceFiles = sourceFiles;
    }

    public GradingLanguage getGradingLanguage() {
        return gradingLanguage;
    }

    public Map<String, SourceFile> getSourceFiles() {
        return sourceFiles;
    }
}
