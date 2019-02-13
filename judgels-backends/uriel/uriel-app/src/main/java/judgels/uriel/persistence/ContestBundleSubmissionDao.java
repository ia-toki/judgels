package judgels.uriel.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;

public interface ContestBundleSubmissionDao extends JudgelsDao<ContestBundleSubmissionModel> {
    List<ContestBundleSubmissionModel> selectByContainerJidAndProblemJidAndCreatedBy(
            String containerJid, String problemJid, String createdBy);
    Optional<ContestBundleSubmissionModel> selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(
            String containerJid, String problemJid, String itemJid, String createdBy);
}
