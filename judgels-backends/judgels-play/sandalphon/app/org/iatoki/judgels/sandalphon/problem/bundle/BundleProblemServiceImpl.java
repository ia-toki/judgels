package org.iatoki.judgels.sandalphon.problem.bundle;

import com.google.gson.Gson;
import judgels.fs.FileSystem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemFileSystemProvider;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemsConfig;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public final class BundleProblemServiceImpl implements BundleProblemService {

    private final FileSystem problemFs;

    @Inject
    public BundleProblemServiceImpl(@ProblemFileSystemProvider FileSystem problemFs) {
        this.problemFs = problemFs;
    }

    @Override
    public void initBundleProblem(String problemJid) throws IOException {
        problemFs.createDirectory(BundleProblemServiceImplUtils.getItemsDirPath(problemFs, problemJid, null));

        BundleItemsConfig config = BundleItemUtils.createDefaultItemConfig();
        problemFs.writeToFile(BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFs, problemJid, null), new Gson().toJson(config));
    }
}
