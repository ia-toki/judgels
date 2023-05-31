package judgels.gabriel.cache;

import java.io.File;
import java.nio.file.Paths;

public class ProblemCache {
    private final CacheConfiguration config;

    public ProblemCache(CacheConfiguration config) {
        this.config = config;
    }

    public File getProblemGradingDir(String problemJid) {
        return config.getCachedBaseDataDir().resolve(Paths.get("problems", problemJid, "grading")).toFile();
    }
}
