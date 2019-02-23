package judgels.uriel.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;

public interface ContestBundleItemSubmissionDao extends JudgelsDao<ContestBundleItemSubmissionModel> {
    List<ContestBundleItemSubmissionModel> selectByContainerJidAndCreatedBy(
            String containerJid,
            String createdBy);

    List<ContestBundleItemSubmissionModel> selectByContainerJidAndProblemJidAndCreatedBy(
            String containerJid,
            String problemJid,
            String createdBy);

    Optional<ContestBundleItemSubmissionModel> selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(
            String containerJid,
            String problemJid,
            String itemJid,
            String createdBy);
}
