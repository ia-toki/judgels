package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.Sandbox;

import java.io.File;
import java.util.Map;

public final class GradingContext {
    private final Language language;
    private final Map<String, File> sourceFiles;
    private final Map<String, File> helperFiles;

    public GradingContext(Language language, Map<String, File> sourceFiles, Map<String, File> helperFiles) {
        this.language = language;
        this.sourceFiles = sourceFiles;
        this.helperFiles = helperFiles;
    }

    public Language getLanguage() {
        return language;
    }

    public Map<String, File> getSourceFiles() {
        return sourceFiles;
    }

    public Map<String, File> getHelperFiles() {
        return helperFiles;
    }
}
