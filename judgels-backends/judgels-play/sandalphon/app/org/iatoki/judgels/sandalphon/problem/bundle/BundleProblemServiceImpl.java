package org.iatoki.judgels.sandalphon.problem.bundle;

import com.google.gson.Gson;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.sandalphon.problem.base.ProblemFileSystemProvider;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemsConfig;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public final class BundleProblemServiceImpl implements BundleProblemService {

    private final FileSystemProvider problemFileSystemProvider;

    @Inject
    public BundleProblemServiceImpl(@ProblemFileSystemProvider FileSystemProvider problemFileSystemProvider) {
        this.problemFileSystemProvider = problemFileSystemProvider;
    }

    @Override
    public void initBundleProblem(String problemJid) throws IOException {
        problemFileSystemProvider.createDirectory(BundleProblemServiceImplUtils.getItemsDirPath(problemFileSystemProvider, problemJid, null));

        BundleItemsConfig config = BundleItemUtils.createDefaultItemConfig();
        problemFileSystemProvider.writeToFile(BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, null), new Gson().toJson(config));
    }
}
