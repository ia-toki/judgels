package judgels.sandalphon.lesson.version;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.List;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.sandalphon.Git;
import judgels.sandalphon.GitCommit;
import judgels.sandalphon.lesson.BaseLessonStore;
import judgels.sandalphon.persistence.LessonDao;
import judgels.sandalphon.persistence.LessonModel;
import judgels.sandalphon.problem.base.ProblemFs;
import judgels.sandalphon.problem.base.ProblemGit;

public class LessonVersionStore extends BaseLessonStore {
    private final Git lessonGit;
    private final LessonDao lessonDao;

    @Inject
    public LessonVersionStore(
            ObjectMapper mapper,
            @ProblemFs FileSystem lessonFs,
            @ProblemGit Git lessonGit,
            LessonDao lessonDao) {

        super(mapper, lessonFs);
        this.lessonGit = lessonGit;
        this.lessonDao = lessonDao;
    }

    public List<GitCommit> getVersions(String userJid, String lessonJid) {
        Path root = getRootDirPath(lessonFs, userJid, lessonJid);
        return lessonGit.getLog(root);
    }

    public boolean commitThenMergeUserClone(String userJid, String lessonJid, String title, String description) {
        Path root = getCloneDirPath(userJid, lessonJid);

        lessonGit.addAll(root);
        lessonGit.commit(root, userJid, "no@email.com", title, description);
        boolean success = lessonGit.rebase(root);

        if (!success) {
            lessonGit.resetToParent(root);
        } else {
            LessonModel lessonModel = lessonDao.findByJid(lessonJid);
            lessonDao.update(lessonModel);
        }

        return success;
    }

    public boolean updateUserClone(String userJid, String lessonJid) {
        Path root = getCloneDirPath(userJid, lessonJid);

        lessonGit.addAll(root);
        lessonGit.commit(root, userJid, "no@email.com", "dummy", "dummy");
        boolean success = lessonGit.rebase(root);

        lessonGit.resetToParent(root);

        return success;
    }

    public boolean pushUserClone(String userJid, String lessonJid) {
        Path origin = getOriginDirPath(lessonJid);
        Path root = getRootDirPath(lessonFs, userJid, lessonJid);

        if (lessonGit.push(root)) {
            lessonGit.resetHard(origin);

            LessonModel lessonModel = lessonDao.findByJid(lessonJid);
            lessonDao.update(lessonModel);

            return true;
        }
        return false;
    }

    public boolean fetchUserClone(String userJid, String lessonJid) {
        Path root = getRootDirPath(lessonFs, userJid, lessonJid);

        return lessonGit.fetch(root);
    }

    public void discardUserClone(String userJid, String lessonJid) {
        Path root = getRootDirPath(lessonFs, userJid, lessonJid);

        lessonFs.removeFile(root);
    }

    public void restore(String lessonJid, String hash) {
        Path root = getOriginDirPath(lessonJid);

        lessonGit.restore(root, hash);

        LessonModel lessonModel = lessonDao.findByJid(lessonJid);
        lessonDao.update(lessonModel);
    }
}
