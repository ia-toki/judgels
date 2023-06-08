package judgels.gabriel.cache;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProblemCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemCache.class);

    private final CacheConfiguration config;

    public ProblemCache(CacheConfiguration config) {
        this.config = config;
    }

    public Path getProblemGradingDir(String problemJid, Instant lastGradingUpdateTimeFromRequest) throws IOException, InterruptedException {
        Path problemGradingDir = config.getCachedBaseDataDir().resolve("problems").resolve(problemJid).resolve("grading");
        Files.createDirectories(problemGradingDir);

        if (shouldSyncProblemGradingDir(problemJid, problemGradingDir, lastGradingUpdateTimeFromRequest)) {
            syncProblemGrading(problemJid);
        }

        return problemGradingDir;
    }

    private synchronized boolean shouldSyncProblemGradingDir(String problemJid, Path problemGradingDir, Instant lastUpdateTimeFromRequest) {
        Path lastUpdateTimeFile = problemGradingDir.resolve("lastUpdateTime.txt");
        if (!Files.exists(lastUpdateTimeFile)) {
            LOGGER.info("Cache for grading dir of problem {} not found!", problemJid);
            return true;
        }

        String lastUpdateTime;
        try {
            lastUpdateTime = Files.readString(lastUpdateTimeFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return true;
        }

        Instant lastUpdateTimeFromCache = Instant.ofEpochMilli(Long.parseLong(lastUpdateTime));
        if (lastUpdateTimeFromCache.isBefore(lastUpdateTimeFromRequest)) {
            LOGGER.info("Cache for grading dir of problem {} is outdated!", problemJid);
            LOGGER.info("    cache:   {}", lastUpdateTimeFromCache);
            LOGGER.info("    request: {}", lastUpdateTimeFromRequest);
            return true;
        }

        return false;
    }

    private synchronized void syncProblemGrading(String problemJid) throws IOException, InterruptedException {
        LOGGER.info("Cache sync for grading dir of problem {} started.", problemJid);

        // rsync lastUpdateTime.txt only after all other files have been rsync-ed
        rsyncProblemGrading(problemJid, true);
        rsyncProblemGrading(problemJid, false);

        LOGGER.info("Cache sync for grading dir of problem {} finished.", problemJid);
    }

    private void rsyncProblemGrading(String problemJid, boolean excludeLastUpdateTime) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("rsync");
        command.add("-avzh");
        command.add("--delete");
        command.addAll(List.of("--rsh", "ssh -o StrictHostKeyChecking=no -i " + config.getRsyncIdentityFile()));

        if (excludeLastUpdateTime) {
            command.addAll(List.of("--exclude", "lastUpdateTime.txt"));
        }

        command.add(config.getServerBaseDataDir() + "/problems/" + problemJid + "/grading/");
        command.add(config.getCachedBaseDataDir() + "/problems/" + problemJid + "/grading");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        LOGGER.info("Running: {}", pb.command().stream().map(c -> c.contains(" ") ? "\"" + c + "\"" : c).collect(joining(" ")));
        Process rsyncProcess = pb.start();

        if (rsyncProcess.waitFor(3, TimeUnit.MINUTES)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(rsyncProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.info("    {}", line);
            }

            if (rsyncProcess.exitValue() == 0) {
                return;
            }
        }

        rsyncProcess.destroy();
        throw new RuntimeException("Cache sync for grading dir of problem " + problemJid + " failed!");
    }
}
