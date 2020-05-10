package org.iatoki.judgels.sandalphon.problem.bundle;

import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.sandalphon.problem.base.ProblemServiceImplUtils;

import java.util.List;

public final class BundleProblemServiceImplUtils {

    private BundleProblemServiceImplUtils() {
        // prevent instantiation
    }

    public static List<String> getItemsDirPath(FileSystemProvider fileSystemProvider, String problemJid, String userJid) {
        return ProblemServiceImplUtils.appendPath(ProblemServiceImplUtils.getRootDirPath(fileSystemProvider, userJid, problemJid), "items");
    }

    public static List<String> getItemsConfigFilePath(FileSystemProvider fileSystemProvider, String problemJid, String userJid) {
        return ProblemServiceImplUtils.appendPath(getItemsDirPath(fileSystemProvider, problemJid, userJid), "items.json");
    }

    public static List<String> getItemDirPath(FileSystemProvider fileSystemProvider, String problemJid, String userJid, String itemJid) {
        return ProblemServiceImplUtils.appendPath(getItemsDirPath(fileSystemProvider, problemJid, userJid), itemJid);
    }

    public static List<String> getItemConfigFilePath(FileSystemProvider fileSystemProvider, String problemJid, String userJid, String itemJid, String languageCode) {
        return ProblemServiceImplUtils.appendPath(getItemDirPath(fileSystemProvider, problemJid, userJid, itemJid), languageCode + ".json");
    }
}
