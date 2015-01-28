package org.iatoki.judgels.gabriel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class GabrielProperties {
    private static GabrielProperties INSTANCE;

    private final File problemDir;
    private final File tempDir;
    private final File workerDir;

    private GabrielProperties(File baseDir) {
        try {
            problemDir = new File(baseDir, "problem");
            FileUtils.forceMkdir(problemDir);
            tempDir = new File(baseDir, "sandbox");
            FileUtils.forceMkdir(tempDir);
            workerDir = new File(baseDir, "temp");
            FileUtils.forceMkdir(workerDir);

        } catch (IOException e) {
            throw new RuntimeException("Cannot create folder inside " + baseDir.getAbsolutePath());
        }
    }

    public File getProblemDir() {
        return problemDir;
    }

    public File getTempDir() {
        return tempDir;
    }

    public File getWorkerDir() {
        return workerDir;
    }

    public static GabrielProperties getInstance() {
        if (INSTANCE == null) {
            InputStream config = ClassLoader.getSystemClassLoader().getResourceAsStream("conf/application.conf");

            if (config == null) {
                throw new RuntimeException("Missing conf/application.conf file");
            }

            Properties properties = new Properties();

            try {
                properties.load(config);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String baseDirName = properties.getProperty("gabriel.baseDir");
            if (baseDirName == null) {
                throw new RuntimeException("Missing gabriel.baseDir properties in conf/application.conf");
            }

            baseDirName = baseDirName.replaceAll("\"", "");

            File baseDir = new File(baseDirName);
            if (!baseDir.isDirectory()) {
                throw new RuntimeException("gabriel.baseDir: " + baseDirName + " does not exist");
            }

            INSTANCE = new GabrielProperties(baseDir);
        }
        return INSTANCE;
    }
}
