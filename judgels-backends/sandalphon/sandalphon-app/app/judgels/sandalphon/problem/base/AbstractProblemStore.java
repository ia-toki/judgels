package judgels.sandalphon.problem.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import judgels.fs.FileSystem;

public abstract class AbstractProblemStore {
    private final ObjectMapper mapper;
    private final FileSystem fs;

    protected AbstractProblemStore(ObjectMapper mapper, FileSystem fs) {
        this.mapper = mapper;
        this.fs = fs;
    }

    protected Path getOriginDirPath(String problemJid) {
        return Paths.get("problems", problemJid);
    }

    protected Path getClonesDirPath(String problemJid) {
        return Paths.get("problem-clones", problemJid);
    }

    protected Path getCloneDirPath(String userJid, String problemJid) {
        return getClonesDirPath(problemJid).resolve(userJid);
    }

    protected Path getRootDirPath(String userJid, String problemJid) {
        Path origin = getOriginDirPath(problemJid);
        if (userJid == null) {
            return origin;
        }

        Path root = getCloneDirPath(userJid, problemJid);
        if (!fs.directoryExists(root)) {
            return origin;
        } else {
            return root;
        }
    }

    protected String writeObj(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
