package judgels.sandalphon.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import judgels.fs.FileSystem;
import judgels.sandalphon.problem.base.BaseProblemStore;

public abstract class BaseBundleProblemStore extends BaseProblemStore {
    protected BaseBundleProblemStore(ObjectMapper mapper, FileSystem fs) {
        super(mapper, fs);
    }

    protected Path getItemsDirPath(String userJid, String problemJid) {
        return getRootDirPath(userJid, problemJid).resolve("items");
    }

    protected Path getItemsConfigFilePath(String userJid, String problemJid) {
        return getItemsDirPath(userJid, problemJid).resolve("items.json");
    }

    protected Path getItemDirPath(String userJid, String problemJid, String itemJid) {
        return getItemsDirPath(userJid, problemJid).resolve(itemJid);
    }

    protected Path getItemConfigFilePath(String userJid, String problemJid, String itemJid, String language) {
        return getItemDirPath(userJid, problemJid, itemJid).resolve(language + ".json");
    }
}
