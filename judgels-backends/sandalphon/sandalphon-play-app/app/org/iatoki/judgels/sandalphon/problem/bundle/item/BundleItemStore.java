package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.persistence.JidGenerator;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.BundleItemsConfig;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.problem.base.ProblemFs;
import judgels.sandalphon.problem.bundle.AbstractBundleProblemStore;
import org.apache.commons.lang3.StringUtils;

public final class BundleItemStore extends AbstractBundleProblemStore {
    private final ObjectMapper mapper;
    private final FileSystem problemFs;

    @Inject
    public BundleItemStore(ObjectMapper mapper, @ProblemFs FileSystem problemFs) {
        super(mapper, problemFs);
        this.mapper = mapper;
        this.problemFs = problemFs;
    }

    public boolean bundleItemExistsInProblemWithCloneByJid(String problemJid, String userJid, String itemJid) {
        return problemFs.directoryExists(getItemDirPath(problemJid, userJid, itemJid));
    }

    public boolean bundleItemExistsInProblemWithCloneByMeta(String problemJid, String userJid, String meta) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);
        for (BundleItem item : config.getItemList()) {
            if (item.getMeta().equals(meta)) {
                return true;
            }
        }
        return false;
    }

    public BundleItem findInProblemWithCloneByItemJid(String problemJid, String userJid, String itemJid) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);
        for (BundleItem bundleItem : config.getItemList()) {
            if (bundleItem.getJid().equals(itemJid)) {
                return bundleItem;
            }
        }
        return null;
    }

    public String getItemConfInProblemWithCloneByJid(String problemJid, String userJid, String itemJid, String language) {
        return problemFs.readFromFile(getItemConfigFilePath(problemJid, userJid, itemJid, language));
    }

    public Page<BundleItem> getPageOfBundleItemsInProblemWithClone(String problemJid, String userJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);
        List<BundleItem> items = config.getItemList();

        List<BundleItem> filteredItems = items.stream()
                .filter(b -> (StringUtils.containsIgnoreCase(b.getMeta(), filterString)) || StringUtils.containsIgnoreCase(b.getJid(), filterString) || StringUtils.containsIgnoreCase(b.getType().name(), filterString))
                .sorted(new BundleItemComparator(orderBy, orderDir))
                .skip((pageIndex - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        ImmutableList.Builder<BundleItem> numberedItems = ImmutableList.builder();

        int number = 1;
        for (BundleItem item : filteredItems) {
            BundleItem.Builder numberedItem = new BundleItem.Builder().from(item);
            if (BundleItemAdapters.fromItemType(item.getType(), mapper) instanceof BundleItemHasScore) {
                numberedItem.number(number++);
            }
            numberedItems.add(numberedItem.build());
        }

        return new Page.Builder<BundleItem>()
                .page(numberedItems.build())
                .totalCount(items.size())
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .build();
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

    public void createBundleItem(String problemJid, String userJid, ItemType itemType, String meta, String conf, String language) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);

        ImmutableList.Builder<BundleItem> items = ImmutableList.builder();
        items.addAll(config.getItemList());

        String itemJid = JidGenerator.generateJid("ITEM");
        items.add(new BundleItem.Builder()
                .jid(itemJid)
                .type(itemType)
                .meta(meta)
                .build());

        config = new BundleItemsConfig.Builder().itemList(items.build()).build();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(config));

        problemFs.createDirectory(getItemDirPath(problemJid, userJid, itemJid));
        problemFs.writeToFile(getItemConfigFilePath(problemJid, userJid, itemJid, language), conf);
    }

    public void updateBundleItem(String problemJid, String userJid, String itemJid, String meta, String conf, String language) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);

        ImmutableList.Builder<BundleItem> items = ImmutableList.builder();
        for (BundleItem item : config.getItemList()) {
            if (item.getJid().equals(itemJid)) {
                items.add(new BundleItem.Builder()
                        .jid(itemJid)
                        .type(item.getType())
                        .meta(meta)
                        .build());
            } else {
                items.add(item);
            }
        }

        config = new BundleItemsConfig.Builder().itemList(items.build()).build();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(config));

        problemFs.writeToFile(getItemConfigFilePath(problemJid, userJid, itemJid, language), conf);
    }

    public void moveBundleItemUp(String problemJid, String userJid, String itemJid) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);

        List<BundleItem> items = Lists.newArrayList(config.getItemList());
        for (int i = 1; i < items.size(); i++) {
            if (items.get(i).getJid().equals(itemJid)) {
                BundleItem current = items.get(i);
                BundleItem previous = items.get(i - 1);

                items.set(i, previous);
                items.set(i - 1, current);
                break;
            }
        }

        config = new BundleItemsConfig.Builder().itemList(items).build();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(config));
    }

    public void moveBundleItemDown(String problemJid, String userJid, String itemJid) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);

        List<BundleItem> items = Lists.newArrayList(config.getItemList());
        for (int i = 0; i + 1 < items.size(); i++) {
            if (items.get(i).getJid().equals(itemJid)) {
                BundleItem current = items.get(i);
                BundleItem next = items.get(i + 1);

                items.set(i, next);
                items.set(i + 1, current);
                break;
            }
        }

        config = new BundleItemsConfig.Builder().itemList(items).build();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(config));
    }

    public void removeBundleItem(String problemJid, String userJid, String itemJid) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);

        ImmutableList.Builder<BundleItem> items = ImmutableList.builder();
        for (BundleItem item : config.getItemList()) {
            if (!item.getJid().equals(itemJid)) {
                items.add(item);
            }
        }

        config = new BundleItemsConfig.Builder().itemList(items.build()).build();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(config));
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
