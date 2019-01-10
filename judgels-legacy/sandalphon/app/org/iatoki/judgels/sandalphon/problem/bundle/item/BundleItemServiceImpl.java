package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.play.jid.JidService;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.sandalphon.problem.base.ProblemFileSystemProvider;
import org.iatoki.judgels.sandalphon.problem.bundle.BundleProblemServiceImplUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class BundleItemServiceImpl implements BundleItemService {

    private final FileSystemProvider problemFileSystemProvider;

    @Inject
    public BundleItemServiceImpl(@ProblemFileSystemProvider FileSystemProvider problemFileSystemProvider) {
        this.problemFileSystemProvider = problemFileSystemProvider;
    }

    @Override
    public boolean bundleItemExistsInProblemWithCloneByJid(String problemJid, String userJid, String itemJid) throws IOException {
        return problemFileSystemProvider.directoryExists(BundleProblemServiceImplUtils.getItemDirPath(problemFileSystemProvider, problemJid, userJid, itemJid));
    }

    @Override
    public boolean bundleItemExistsInProblemWithCloneByMeta(String problemJid, String userJid, String meta) throws IOException {
        List<String> itemsConfig = BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid);
        BundleItemsConfig bundleItemsConfig = new Gson().fromJson(problemFileSystemProvider.readFromFile(itemsConfig), BundleItemsConfig.class);

        for (BundleItem bundleItem : bundleItemsConfig.itemList) {
            if (bundleItem.getMeta().equals(meta)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BundleItem findInProblemWithCloneByItemJid(String problemJid, String userJid, String itemJid) throws IOException {
        List<String> itemsConfig = BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid);
        BundleItemsConfig bundleItemsConfig = new Gson().fromJson(problemFileSystemProvider.readFromFile(itemsConfig), BundleItemsConfig.class);

        for (BundleItem bundleItem : bundleItemsConfig.itemList) {
            if (bundleItem.getJid().equals(itemJid)) {
                return bundleItem;
            }
        }

        return null;
    }

    @Override
    public String getItemConfInProblemWithCloneByJid(String problemJid, String userJid, String itemJid, String languageCode) throws IOException {
        return problemFileSystemProvider.readFromFile(BundleProblemServiceImplUtils.getItemConfigFilePath(problemFileSystemProvider, problemJid, userJid, itemJid, languageCode));
    }

    @Override
    public Page<BundleItem> getPageOfBundleItemsInProblemWithClone(String problemJid, String userJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) throws IOException {
        List<String> itemsConfig = BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid);
        BundleItemsConfig bundleItemsConfig = new Gson().fromJson(problemFileSystemProvider.readFromFile(itemsConfig), BundleItemsConfig.class);
        List<BundleItem> bundleItems = bundleItemsConfig.itemList;

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

        return new Page<>(filteredBundleItems, bundleItems.size(), pageIndex, pageSize);
    }

    @Override
    public List<BundleItem> getBundleItemsInProblemWithClone(String problemJid, String userJid) throws IOException {
        List<String> itemsConfig = BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid);
        BundleItemsConfig bundleItemsConfig = new Gson().fromJson(problemFileSystemProvider.readFromFile(itemsConfig), BundleItemsConfig.class);

        List<BundleItem> bundleItems = bundleItemsConfig.itemList.stream().collect(Collectors.toList());
        long number = 1;
        for (BundleItem bundleItem : bundleItems) {
            if (BundleItemAdapters.fromItemType(bundleItem.getType()) instanceof BundleItemHasScore) {
                bundleItem.setNumber(number++);
            }
        }

        return bundleItems;
    }

    @Override
    public void createBundleItem(String problemJid, String userJid, BundleItemType itemType, String meta, String conf, String languageCode) throws IOException {
        List<String> itemsConfig = BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid);
        BundleItemsConfig bundleItemsConfig = new Gson().fromJson(problemFileSystemProvider.readFromFile(itemsConfig), BundleItemsConfig.class);
        List<BundleItem> bundleItems = Lists.newArrayList(bundleItemsConfig.itemList);

        String itemJid = JidService.getInstance().generateNewJid("ITEM");
        BundleItem bundleItem = new BundleItem(itemJid, itemType, meta);
        bundleItems.add(bundleItem);

        bundleItemsConfig.itemList = bundleItems;
        problemFileSystemProvider.writeToFile(BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid), new Gson().toJson(bundleItemsConfig));

        problemFileSystemProvider.createDirectory(BundleProblemServiceImplUtils.getItemDirPath(problemFileSystemProvider, problemJid, userJid, itemJid));
        problemFileSystemProvider.writeToFile(BundleProblemServiceImplUtils.getItemConfigFilePath(problemFileSystemProvider, problemJid, userJid, itemJid, languageCode), conf);
    }

    @Override
    public void updateBundleItem(String problemJid, String userJid, String itemJid, String meta, String conf, String languageCode) throws IOException {
        List<String> itemsConfig = BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid);
        BundleItemsConfig bundleItemsConfig = new Gson().fromJson(problemFileSystemProvider.readFromFile(itemsConfig), BundleItemsConfig.class);
        List<BundleItem> bundleItems = Lists.newArrayList(bundleItemsConfig.itemList);

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

        bundleItemsConfig.itemList = bundleItems;
        problemFileSystemProvider.writeToFile(BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid), new Gson().toJson(bundleItemsConfig));

        problemFileSystemProvider.writeToFile(BundleProblemServiceImplUtils.getItemConfigFilePath(problemFileSystemProvider, problemJid, userJid, itemJid, languageCode), conf);
    }

    @Override
    public void moveBundleItemUp(String problemJid, String userJid, String itemJid) throws IOException {
        List<String> itemsConfig = BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid);
        BundleItemsConfig bundleItemsConfig = new Gson().fromJson(problemFileSystemProvider.readFromFile(itemsConfig), BundleItemsConfig.class);
        List<BundleItem> bundleItems = Lists.newArrayList(bundleItemsConfig.itemList);

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

        bundleItemsConfig.itemList = bundleItems;
        problemFileSystemProvider.writeToFile(BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid), new Gson().toJson(bundleItemsConfig));
    }

    @Override
    public void moveBundleItemDown(String problemJid, String userJid, String itemJid) throws IOException {
        List<String> itemsConfig = BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid);
        BundleItemsConfig bundleItemsConfig = new Gson().fromJson(problemFileSystemProvider.readFromFile(itemsConfig), BundleItemsConfig.class);
        List<BundleItem> bundleItems = Lists.newArrayList(bundleItemsConfig.itemList);

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

        bundleItemsConfig.itemList = bundleItems;
        problemFileSystemProvider.writeToFile(BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid), new Gson().toJson(bundleItemsConfig));
    }

    @Override
    public void removeBundleItem(String problemJid, String userJid, String itemJid) throws IOException {
        List<String> itemsConfig = BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid);
        BundleItemsConfig bundleItemsConfig = new Gson().fromJson(problemFileSystemProvider.readFromFile(itemsConfig), BundleItemsConfig.class);
        List<BundleItem> bundleItems = Lists.newArrayList(bundleItemsConfig.itemList);

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

        bundleItemsConfig.itemList = bundleItems;
        problemFileSystemProvider.writeToFile(BundleProblemServiceImplUtils.getItemsConfigFilePath(problemFileSystemProvider, problemJid, userJid), new Gson().toJson(bundleItemsConfig));
    }
}
