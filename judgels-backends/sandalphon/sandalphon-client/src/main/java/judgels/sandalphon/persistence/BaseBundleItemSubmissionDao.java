package judgels.sandalphon.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface BaseBundleItemSubmissionDao<M extends AbstractBundleItemSubmissionModel> extends JudgelsDao<M> {
    M createSubmissionModel();

    Page<M> selectPaged(
            String containerJid,
            Optional<String> createdBy,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            SelectionOptions options);

    List<M> selectAllByContainerJid(
            String createdBy);

    List<M> selectAllByContainerJidAndCreatedBy(
            String containerJid,
            String createdBy);

    List<M> selectAllByContainerJidAndProblemJidAndCreatedBy(
            String containerJid,
            String problemJid,
            String createdBy);

    List<M> selectAllForRegrade(
            Optional<String> containerJid,
            Optional<String> problemJid,
            Optional<String> createdBy);

    Optional<M> selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(
            String containerJid,
            String problemJid,
            String itemJid,
            String createdBy);
}
