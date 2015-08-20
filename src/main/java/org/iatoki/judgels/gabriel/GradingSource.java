package org.iatoki.judgels.gabriel;

import java.io.File;
import java.util.Map;

public final class GradingSource {

    private final Map<String, File> sourceFiles;
    private final Map<String, File> testDataFiles;
    private final Map<String, File> helperFiles;

    public GradingSource(Map<String, File> sourceFiles, Map<String, File> testDataFiles, Map<String, File> helperFiles) {
        this.sourceFiles = sourceFiles;
        this.testDataFiles = testDataFiles;
        this.helperFiles = helperFiles;
    }

    public Map<String, File> getSourceFiles() {
        return sourceFiles;
    }

    public Map<String, File> getTestDataFiles() {
        return testDataFiles;
    }

    public Map<String, File> getHelperFiles() {
        return helperFiles;
    }
}
