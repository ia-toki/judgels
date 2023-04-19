package judgels.sandalphon.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.persistence.JidGenerator;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.BundleItemsConfig;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.problem.base.ProblemFs;
import judgels.sandalphon.problem.bundle.BaseBundleProblemStore;

public class BundleItemStore extends BaseBundleProblemStore {
    private final ObjectMapper mapper;
    private final FileSystem problemFs;

    @Inject
    public BundleItemStore(ObjectMapper mapper, @ProblemFs FileSystem problemFs) {
        super(mapper, problemFs);
        this.mapper = mapper;
        this.problemFs = problemFs;
    }

    public BundleItem createItem(String userJid, String problemJid, ItemType type, ItemConfig config, String language) {
        List<BundleItem> items = new ArrayList<>(getItems(userJid, problemJid));

        String itemJid = JidGenerator.generateJid("ITEM");
        BundleItem createdItem = new BundleItem.Builder()
                .jid(itemJid)
                .type(type)
                .meta("")
                .build();

        items.add(createdItem);

        BundleItemsConfig itemsConfig = new BundleItemsConfig.Builder().itemList(items).build();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(itemsConfig));

        problemFs.createDirectory(getItemDirPath(problemJid, userJid, itemJid));
        problemFs.writeToFile(getItemConfigFilePath(problemJid, userJid, itemJid, language), writeObj(config));

        return createdItem;
    }

    public List<BundleItem> getNumberedItems(String userJid, String problemJid) {
        List<BundleItem> items = getItems(userJid, problemJid);
        List<BundleItem> numberedItems = new ArrayList<>();

        int number = 1;
        for (BundleItem item : items) {
            BundleItem.Builder numberedItem = new BundleItem.Builder().from(item);
            if (!(ItemEngineRegistry.getByType(item.getType()) instanceof StatementItemEngine)) {
                numberedItem.number(number++);
            }
            numberedItems.add(numberedItem.build());
        }

        return ImmutableList.copyOf(numberedItems);
    }

    public Optional<BundleItem> getNumberedItem(String userJid, String problemJid, String itemJid) {
        return getNumberedItems(userJid, problemJid).stream()
                .filter(item -> item.getJid().equals(itemJid))
                .findFirst();
    }

    public void updateItem(String userJid, String problemJid, BundleItem item, String meta, ItemConfig config, String language) {
        List<BundleItem> items = new ArrayList<>(getItems(userJid, problemJid));
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getJid().equals(item.getJid())) {
                items.set(i, new BundleItem.Builder()
                        .from(items.get(i))
                        .meta(meta)
                        .build());
            }
        }

        BundleItemsConfig itemsConfig = new BundleItemsConfig.Builder().itemList(items).build();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(itemsConfig));

        problemFs.writeToFile(getItemConfigFilePath(problemJid, userJid, item.getJid(), language), writeObj(config));
    }

    public void moveItemUp(String userJid, String problemJid, String itemJid) {
        List<BundleItem> items = new ArrayList<>(getItems(userJid, problemJid));
        for (int i = 1; i < items.size(); i++) {
            if (items.get(i).getJid().equals(itemJid)) {
                BundleItem current = items.get(i);
                BundleItem previous = items.get(i - 1);

                items.set(i, previous);
                items.set(i - 1, current);
                break;
            }
        }

        BundleItemsConfig itemsConfig = new BundleItemsConfig.Builder().itemList(items).build();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(itemsConfig));
    }

    public void moveItemDown(String userJid, String problemJid, String itemJid) {
        List<BundleItem> items = new ArrayList<>(getItems(userJid, problemJid));
        for (int i = 0; i + 1 < items.size(); i++) {
            if (items.get(i).getJid().equals(itemJid)) {
                BundleItem current = items.get(i);
                BundleItem next = items.get(i + 1);

                items.set(i, next);
                items.set(i + 1, current);
                break;
            }
        }

        BundleItemsConfig itemsConfig = new BundleItemsConfig.Builder().itemList(items).build();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(itemsConfig));
    }

    public void removeItem(String userJid, String problemJid, String itemJid) {
        List<BundleItem> items = new ArrayList<>(getItems(userJid, problemJid));
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getJid().equals(itemJid)) {
                items.remove(i);
                break;
            }
        }

        BundleItemsConfig itemsConfig = new BundleItemsConfig.Builder().itemList(items).build();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, userJid), writeObj(itemsConfig));
    }

    public ItemConfig getItemConfig(String userJid, String problemJid, BundleItem item, String language, String defaultLanguage) {
        String config;

        try {
            config = problemFs.readFromFile(getItemConfigFilePath(problemJid, userJid, item.getJid(), language));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                try {
                    config = problemFs.readFromFile(getItemConfigFilePath(problemJid, userJid, item.getJid(), defaultLanguage));
                } catch (RuntimeException e2) {
                    if (e2.getCause() instanceof IOException) {
                        return ItemEngineRegistry.getByType(item.getType()).createDefaultConfig();
                    }
                    throw e2;
                }
            } else {
                throw e;
            }
        }

        try {
            return ItemEngineRegistry.getByType(item.getType()).parseConfig(mapper, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private List<BundleItem> getItems(String userJid, String problemJid) {
        Path itemsConfigPath = getItemsConfigFilePath(problemJid, userJid);
        try {
            return mapper.readValue(problemFs.readFromFile(itemsConfigPath), BundleItemsConfig.class).getItemList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
