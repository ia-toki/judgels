package judgels.uriel.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestBundleItemSubmissionDao extends JudgelsDao<ContestBundleItemSubmissionModel> {
    Page<ContestBundleItemSubmissionModel> selectPaged(
            String containerJid,
            Optional<String> createdBy,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            SelectionOptions options);

    List<ContestBundleItemSubmissionModel> selectAllByContainerJid(
            String createdBy);

    List<ContestBundleItemSubmissionModel> selectAllByContainerJidAndCreatedBy(
            String containerJid,
            String createdBy);

    List<ContestBundleItemSubmissionModel> selectAllByContainerJidAndProblemJidAndCreatedBy(
            String containerJid,
            String problemJid,
            String createdBy);

    Optional<ContestBundleItemSubmissionModel> selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(
            String containerJid,
            String problemJid,
            String itemJid,
            String createdBy);
}
