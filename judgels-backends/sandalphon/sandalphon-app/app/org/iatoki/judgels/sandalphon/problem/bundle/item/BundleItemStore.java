package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.persistence.JidGenerator;
import judgels.persistence.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemFs;
import org.iatoki.judgels.sandalphon.problem.bundle.AbstractBundleProblemStore;

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
        for (BundleItem bundleItem : config.itemList) {
            if (bundleItem.getMeta().equals(meta)) {
                return true;
            }
        }
        return false;
    }

    public BundleItem findInProblemWithCloneByItemJid(String problemJid, String userJid, String itemJid) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);
        for (BundleItem bundleItem : config.itemList) {
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
        List<BundleItem> bundleItems = config.itemList;

        List<BundleItem> filteredBundleItems = bundleItems.stream()
                .filter(b -> (StringUtils.containsIgnoreCase(b.getMeta(), filterString)) || StringUtils.containsIgnoreCase(b.getJid(), filterString) || StringUtils.containsIgnoreCase(b.getType().name(), filterString))
                .sorted(new BundleItemComparator(orderBy, orderDir))
                .skip(pageIndex * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        long number = 1;
        for (BundleItem bundleItem : filteredBundleItems) {
            if (BundleItemAdapters.fromItemType(bundleItem.getType()) instanceof BundleItemHasScore) {
                bundleItem.setNumber(number++);
            }
        }

        return new Page.Builder<BundleItem>()
                .page(filteredBundleItems)
                .totalCount(bundleItems.size())
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .build();
    }

    public List<BundleItem> getBundleItemsInProblemWithClone(String problemJid, String userJid) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);
        List<BundleItem> bundleItems = config.itemList.stream().collect(Collectors.toList());

        long number = 1;
        for (int i = 0; i < bundleItems.size(); i++) {
            if (BundleItemAdapters.fromItemType(bundleItems.get(i).getType()) instanceof BundleItemHasScore) {
                bundleItems.get(i).setNumber(number++);
            }
        }

        return bundleItems;
    }

    public void createBundleItem(String problemJid, String userJid, BundleItemType itemType, String meta, String conf, String language) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);
        List<BundleItem> bundleItems = Lists.newArrayList(config.itemList);

        String itemJid = JidGenerator.generateJid("ITEM");
        BundleItem bundleItem = new BundleItem(itemJid, itemType, meta);
        bundleItems.add(bundleItem);

        config.itemList = bundleItems;
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(config));

        problemFs.createDirectory(getItemDirPath(problemJid, userJid, itemJid));
        problemFs.writeToFile(getItemConfigFilePath(problemJid, userJid, itemJid, language), conf);
    }

    public void updateBundleItem(String problemJid, String userJid, String itemJid, String meta, String conf, String language) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);
        List<BundleItem> bundleItems = Lists.newArrayList(config.itemList);

        int i = 0;
        if (bundleItems.size() > 0) {
            do {
                if (bundleItems.get(i).getJid().equals(itemJid)) {
                    BundleItem current = bundleItems.get(i);
                    bundleItems.set(i, new BundleItem(current.getJid(), current.getType(), meta));
                }
                ++i;
            } while ((i < bundleItems.size()) && !bundleItems.get(i - 1).getJid().equals(itemJid));
        }

        config.itemList = bundleItems;
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(config));

        problemFs.writeToFile(getItemConfigFilePath(problemJid, userJid, itemJid, language), conf);
    }

    public void moveBundleItemUp(String problemJid, String userJid, String itemJid) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);
        List<BundleItem> bundleItems = Lists.newArrayList(config.itemList);

        int i = 1;
        if (bundleItems.size() > 0) {
            do {
                if (bundleItems.get(i).getJid().equals(itemJid)) {
                    BundleItem current = bundleItems.get(i);
                    BundleItem previous = bundleItems.get(i - 1);
                    bundleItems.set(i, new BundleItem(previous.getJid(), previous.getType(), previous.getMeta()));
                    bundleItems.set(i - 1, new BundleItem(current.getJid(), current.getType(), current.getMeta()));
                }
                ++i;
            } while ((i < bundleItems.size()) && !bundleItems.get(i - 1).getJid().equals(itemJid));
        }

        config.itemList = bundleItems;
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(config));
    }

    public void moveBundleItemDown(String problemJid, String userJid, String itemJid) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);
        List<BundleItem> bundleItems = Lists.newArrayList(config.itemList);

        int i = 0;
        if (bundleItems.size() > 0) {
            do {
                if (bundleItems.get(i).getJid().equals(itemJid)) {
                    BundleItem current = bundleItems.get(i);
                    BundleItem next = bundleItems.get(i + 1);
                    bundleItems.set(i, new BundleItem(next.getJid(), next.getType(), next.getMeta()));
                    bundleItems.set(i + 1, new BundleItem(current.getJid(), current.getType(), current.getMeta()));
                }
                ++i;
            } while ((i < bundleItems.size() - 1) && !bundleItems.get(i - 1).getJid().equals(itemJid));
        }

        config.itemList = bundleItems;
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(config));
    }

    public void removeBundleItem(String problemJid, String userJid, String itemJid) {
        BundleItemsConfig config = getItemsConfig(problemJid, userJid);
        List<BundleItem> bundleItems = Lists.newArrayList(config.itemList);

        int toBeRemovedIndex = -1;
        int i = 0;
        if (bundleItems.size() > 0) {
            do {
                if (bundleItems.get(i).getJid().equals(itemJid)) {
                    toBeRemovedIndex = i;
                }
                ++i;
            } while ((i < bundleItems.size()) && !bundleItems.get(i - 1).getJid().equals(itemJid));

            if (toBeRemovedIndex != -1) {
                bundleItems.remove(toBeRemovedIndex);
            }
        }

        config.itemList = bundleItems;
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
