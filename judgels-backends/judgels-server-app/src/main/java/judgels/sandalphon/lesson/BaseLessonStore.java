package judgels.sandalphon.lesson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import judgels.fs.FileSystem;

public abstract class BaseLessonStore {
    protected final ObjectMapper mapper;
    protected final FileSystem lessonFs;

    public BaseLessonStore(ObjectMapper mapper, FileSystem lessonFs) {
        this.mapper = mapper;
        this.lessonFs = lessonFs;
    }

    protected Path getOriginDirPath(String lessonJid) {
        return Paths.get("lessons", lessonJid);
    }

    protected Path getClonesDirPath(String lessonJid) {
        return Paths.get("lesson-clones", lessonJid);
    }

    protected Path getCloneDirPath(String userJid, String lessonJid) {
        return getClonesDirPath(lessonJid).resolve(userJid);
    }

    protected Path getRootDirPath(FileSystem fs, String userJid, String lessonJid) {
        Path origin = getOriginDirPath(lessonJid);
        if (userJid == null) {
            return origin;
        }

        Path root = getCloneDirPath(userJid, lessonJid);
        if (!lessonFs.directoryExists(root)) {
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
