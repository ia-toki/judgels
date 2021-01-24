package org.iatoki.judgels.sandalphon.problem.bundle;

import com.google.gson.Gson;
import java.io.IOException;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemFs;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemsConfig;

public final class BundleProblemStore {

    private final FileSystem problemFs;

    @Inject
    public BundleProblemStore(@ProblemFs FileSystem problemFs) {
        this.problemFs = problemFs;
    }

    public void initBundleProblem(String problemJid) throws IOException {
        problemFs.createDirectory(BundleProblemServiceImplUtils.getItemsDirPath(problemFs, problemJid, null));

        BundleItemsConfig config = BundleItemUtils.createDefaultItemConfig();
        problemFs.writeToFile(BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFs, problemJid, null), new Gson().toJson(config));
    }
}
