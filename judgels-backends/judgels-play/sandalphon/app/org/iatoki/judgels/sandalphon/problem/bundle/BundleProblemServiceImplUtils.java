package org.iatoki.judgels.sandalphon.problem.bundle;

import java.nio.file.Path;
import judgels.fs.FileSystem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemServiceImplUtils;

public final class BundleProblemServiceImplUtils {

    private BundleProblemServiceImplUtils() {
        // prevent instantiation
    }

    public static Path getItemsDirPath(FileSystem fs, String problemJid, String userJid) {
        return ProblemServiceImplUtils.getRootDirPath(fs, userJid, problemJid).resolve("items");
    }

    public static Path getItemsConfigFilePath(FileSystem fs, String problemJid, String userJid) {
        return getItemsDirPath(fs, problemJid, userJid).resolve("items.json");
    }

    public static Path getItemDirPath(FileSystem fs, String problemJid, String userJid, String itemJid) {
        return getItemsDirPath(fs, problemJid, userJid).resolve(itemJid);
    }

    public static Path getItemConfigFilePath(FileSystem fs, String problemJid, String userJid, String itemJid, String languageCode) {
        return getItemDirPath(fs, problemJid, userJid, itemJid).resolve(languageCode + ".json");
    }
}
