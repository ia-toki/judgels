package org.iatoki.judgels;

import java.util.List;

public interface GitProvider {

    void init(List<String> rootDirPath);

    void clone(List<String> originDirPath, List<String> rootDirPath);

    boolean fetch(List<String> rootDirPath);

    void addAll(List<String> rootDirPath);

    void commit(List<String> rootDirPath, String committerName, String committerEmail, String title, String description);

    boolean rebase(List<String> rootDirPath);

    boolean push(List<String> rootDirPath);

    void resetToParent(List<String> rootDirPath);

    void resetHard(List<String> rootDirPath);

    List<GitCommit> getLog(List<String> rootDirPath);

    void restore(List<String> rootDirPath, String hash);

    String getCommitDiff(List<String> rootDirPath, String commitHash);
}
