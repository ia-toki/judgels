package judgels.sandalphon.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import judgels.fs.FileSystem;
import judgels.sandalphon.problem.base.BaseProblemStore;

public abstract class BaseBundleProblemStore extends BaseProblemStore {
    protected BaseBundleProblemStore(ObjectMapper mapper, FileSystem fs) {
        super(mapper, fs);
    }

    protected Path getItemsDirPath(String problemJid, String userJid) {
        return getRootDirPath(userJid, problemJid).resolve("items");
    }

    protected Path getItemsConfigFilePath(String problemJid, String userJid) {
        return getItemsDirPath(problemJid, userJid).resolve("items.json");
    }

    protected Path getItemDirPath(String problemJid, String userJid, String itemJid) {
        return getItemsDirPath(problemJid, userJid).resolve(itemJid);
    }

    protected Path getItemConfigFilePath(String problemJid, String userJid, String itemJid, String language) {
        return getItemDirPath(problemJid, userJid, itemJid).resolve(language + ".json");
    }
}
