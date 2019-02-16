package judgels.uriel.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestBundleSubmissionDao extends JudgelsDao<ContestBundleSubmissionModel> {
    List<ContestBundleSubmissionModel> selectByContainerJidAndProblemJidAndCreatedBy(
            String containerJid,
            String problemJid,
            String createdBy);

    Optional<ContestBundleSubmissionModel> selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(
            String containerJid,
            String problemJid,
            String itemJid,
            String createdBy);

    Page<ContestBundleSubmissionModel> selectPaged(
            String containerJid,
            Optional<String> createdBy,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            SelectionOptions options);
}
