package judgels.sandalphon.problem.base.version;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.List;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.sandalphon.Git;
import judgels.sandalphon.GitCommit;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemModel;
import judgels.sandalphon.problem.base.BaseProblemStore;
import judgels.sandalphon.problem.base.ProblemFs;
import judgels.sandalphon.problem.base.ProblemGit;

public class ProblemVersionStore extends BaseProblemStore {
    private final Git problemGit;
    private final ProblemDao problemDao;

    @Inject
    public ProblemVersionStore(
            ObjectMapper mapper,
            @ProblemFs FileSystem problemFs,
            @ProblemGit Git problemGit,
            ProblemDao problemDao) {

        super(mapper, problemFs);
        this.problemGit = problemGit;
        this.problemDao = problemDao;
    }

    public List<GitCommit> getVersions(String userJid, String problemJid) {
        Path root = getRootDirPath(userJid, problemJid);
        return problemGit.getLog(root);
    }

    public boolean commitThenMergeUserClone(String userJid, String problemJid, String title, String text) {
        Path root = getCloneDirPath(userJid, problemJid);

        problemGit.addAll(root);
        problemGit.commit(root, userJid, "no@email.com", title, text);
        boolean success = problemGit.rebase(root);

        if (!success) {
            problemGit.resetToParent(root);
        } else {
            ProblemModel model = problemDao.findByJid(problemJid);
            problemDao.update(model);
        }

        return success;
    }

    public boolean updateUserClone(String userJid, String problemJid) {
        Path root = getCloneDirPath(userJid, problemJid);

        problemGit.addAll(root);
        problemGit.commit(root, userJid, "no@email.com", "dummy", "dummy");
        boolean success = problemGit.rebase(root);

        problemGit.resetToParent(root);

        return success;
    }

    public boolean pushUserClone(String userJid, String problemJid) {
        Path origin = getOriginDirPath(problemJid);
        Path root = getCloneDirPath(userJid, problemJid);

        if (problemGit.push(root)) {
            problemGit.resetHard(origin);

            ProblemModel model = problemDao.findByJid(problemJid);
            problemDao.update(model);

            return true;
        }
        return false;
    }

    public boolean fetchUserClone(String userJid, String problemJid) {
        Path root = getCloneDirPath(userJid, problemJid);

        return problemGit.fetch(root);
    }

    public void discardUserClone(String userJid, String problemJid) {
        Path root = getCloneDirPath(userJid, problemJid);

        problemFs.removeFile(root);
    }

    public void restore(String problemJid, String hash) {
        Path root = getOriginDirPath(problemJid);

        problemGit.restore(root, hash);

        ProblemModel model = problemDao.findByJid(problemJid);
        problemDao.update(model);
    }
}
