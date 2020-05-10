package org.iatoki.judgels.sandalphon.problem.base;

import com.google.common.collect.Lists;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.sandalphon.SandalphonProperties;

import java.util.List;

public final class ProblemServiceImplUtils {

    private ProblemServiceImplUtils() {
        // prevent instantiation
    }

    public static List<String> getOriginDirPath(String problemJid) {
        return Lists.newArrayList(SandalphonProperties.getInstance().getBaseProblemsDirKey(), problemJid);
    }

    public static List<String> getClonesDirPath(String problemJid) {
        return Lists.newArrayList(SandalphonProperties.getInstance().getBaseProblemClonesDirKey(), problemJid);
    }

    public static List<String> getCloneDirPath(String userJid, String problemJid) {
        return appendPath(getClonesDirPath(problemJid), userJid);
    }

    public static List<String> getRootDirPath(FileSystemProvider fileSystemProvider, String userJid, String problemJid) {
        List<String> origin =  getOriginDirPath(problemJid);
        List<String> root = getCloneDirPath(userJid, problemJid);

        if (userJid == null || !fileSystemProvider.directoryExists(root)) {
            return origin;
        } else {
            return root;
        }
    }

    public static List<String> appendPath(List<String> parentPath, String child) {
        parentPath.add(child);
        return parentPath;
    }
}
