package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.BundleItemsConfig;
import judgels.sandalphon.problem.base.ProblemFs;
import judgels.sandalphon.problem.bundle.BaseBundleProblemStore;

public final class BundleItemStore extends BaseBundleProblemStore {
    private final ObjectMapper mapper;
    private final FileSystem problemFs;

    @Inject
    public BundleItemStore(ObjectMapper mapper, @ProblemFs FileSystem problemFs) {
        super(mapper, problemFs);
        this.mapper = mapper;
        this.problemFs = problemFs;
    }

    public String getItemConfInProblemWithCloneByJid(String problemJid, String userJid, String itemJid, String language) {
        return problemFs.readFromFile(getItemConfigFilePath(problemJid, userJid, itemJid, language));
    }

    public List<BundleItem> getBundleItemsInProblemWithClone(String problemJid, String userJid) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);
        List<BundleItem> items = config.getItemList();

        ImmutableList.Builder<BundleItem> numberedItems = ImmutableList.builder();

        int number = 1;
        for (BundleItem item : items) {
            BundleItem.Builder numberedItem = new BundleItem.Builder().from(item);
            if (BundleItemAdapters.fromItemType(item.getType(), mapper) instanceof BundleItemHasScore) {
                numberedItem.number(number++);
            }
            numberedItems.add(numberedItem.build());
        }

        return numberedItems.build();
    }

    private BundleItemsConfig getItemsConfig(String problemJid, String userJid) {
        Path itemsConfigPath = getItemsConfigFilePath(problemJid, userJid);
        try {
            return mapper.readValue(problemFs.readFromFile(itemsConfigPath), BundleItemsConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
