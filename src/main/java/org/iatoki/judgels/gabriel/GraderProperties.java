package org.iatoki.judgels.gabriel;

import java.io.File;

public final class GraderProperties {
    private static GraderProperties INSTANCE;

    private final File baseDir;

    private GraderProperties(File baseDir) {
        this.baseDir = baseDir;
    }

    public File getGradingCacheDir() {
        return new File(baseDir, "gradingCache");
    }

    public File getSandboxDir() {
        return new File(baseDir, "sandbox");
    }

    public File getTempDir() {
        return new File(baseDir, "temp");
    }

    public static GraderProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GraderProperties(new File("/Users/fushar/gabriel-data"));
        }
        return INSTANCE;
    }
}
