package judgels.sandalphon.persistence;

import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface BaseSubmissionDao<M extends AbstractSubmissionModel> extends JudgelsDao<M> {
    M createSubmissionModel();
    Page<M> selectPaged(String containerJid, String userJid, SelectionOptions options);
}
