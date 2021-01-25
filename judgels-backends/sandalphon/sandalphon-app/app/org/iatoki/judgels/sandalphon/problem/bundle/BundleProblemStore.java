package org.iatoki.judgels.sandalphon.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.io.IOException;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemFs;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemsConfig;

public final class BundleProblemStore extends AbstractBundleProblemStore {
    private final ObjectMapper mapper;
    private final FileSystem problemFs;

    @Inject
    public BundleProblemStore(ObjectMapper mapper, @ProblemFs FileSystem problemFs) {
        super(mapper, problemFs);
        this.mapper = mapper;
        this.problemFs = problemFs;
    }

    public void initBundleProblem(String problemJid) {
        problemFs.createDirectory(getItemsDirPath(problemJid, null));

        BundleItemsConfig config = createDefaultItemConfig();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, null), writeItemsConfig(config));
    }

    private String writeItemsConfig(BundleItemsConfig config) {
        try {
            return mapper.writeValueAsString(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BundleItemsConfig createDefaultItemConfig() {
        BundleItemsConfig itemConfig = new BundleItemsConfig();
        itemConfig.itemList = Lists.newArrayList();

        return itemConfig;
    }
}
