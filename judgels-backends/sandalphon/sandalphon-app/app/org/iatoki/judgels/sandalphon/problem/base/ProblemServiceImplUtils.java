package org.iatoki.judgels.sandalphon.problem.base;

import java.nio.file.Path;
import java.nio.file.Paths;
import judgels.fs.FileSystem;
import org.iatoki.judgels.sandalphon.SandalphonProperties;

public final class ProblemServiceImplUtils {

    private ProblemServiceImplUtils() {
        // prevent instantiation
    }

    public static Path getOriginDirPath(String problemJid) {
        return Paths.get(SandalphonProperties.getInstance().getBaseProblemsDirKey(), problemJid);
    }

    public static Path getClonesDirPath(String problemJid) {
        return Paths.get(SandalphonProperties.getInstance().getBaseProblemClonesDirKey(), problemJid);
    }

    public static Path getCloneDirPath(String userJid, String problemJid) {
        return getClonesDirPath(problemJid).resolve(userJid);
    }

    public static Path getRootDirPath(FileSystem fs, String userJid, String problemJid) {
        Path origin = getOriginDirPath(problemJid);
        if (userJid == null) {
            return origin;
        }

        Path root = getCloneDirPath(userJid, problemJid);
        if (!fs.directoryExists(root)) {
            return origin;
        } else {
            return root;
        }
    }
}
