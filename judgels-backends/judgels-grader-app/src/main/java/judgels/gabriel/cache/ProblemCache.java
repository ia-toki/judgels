package judgels.gabriel.cache;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ProblemCache {
    private final CacheConfiguration config;

    public ProblemCache(CacheConfiguration config) {
        this.config = config;
    }

    public Path getProblemGradingDir(String problemJid) {
        return config.getCachedBaseDataDir().resolve(Paths.get("problems", problemJid, "grading"));
    }
}
