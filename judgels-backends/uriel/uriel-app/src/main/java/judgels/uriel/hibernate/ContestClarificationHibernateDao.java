package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.persistence.ContestClarificationModel;
import judgels.uriel.persistence.ContestClarificationModel_;

@Singleton
public class ContestClarificationHibernateDao extends JudgelsHibernateDao<ContestClarificationModel> implements
        ContestClarificationDao {

    @Inject
    public ContestClarificationHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Page<ContestClarificationModel> selectPagedByContestJidAndUserJid(
            String contestJid,
            String userJid,
            SelectionOptions options) {

        return selectPaged(new FilterOptions.Builder<ContestClarificationModel>()
                .putColumnsEq(ContestClarificationModel_.contestJid, contestJid)
                .putColumnsEq(ContestClarificationModel_.createdBy, userJid)
                .build(), options);
    }

    @Override
    public Page<ContestClarificationModel> selectPagedByContestJid(String contestJid, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<ContestClarificationModel>()
                .putColumnsEq(ContestClarificationModel_.contestJid, contestJid)
                .build(), options);
    }

    @Override
    public Page<ContestClarificationModel> selectPagedByContestJidAndStatus(
            String contestJid, String status, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<ContestClarificationModel>()
                .putColumnsEq(ContestClarificationModel_.contestJid, contestJid)
                .putColumnsEq(ContestClarificationModel_.status, status)
                .build(), options);
    }

    @Override
    public Set<ContestClarificationModel> selectAllByContestJid(String contestJid, SelectionOptions options) {
        return ImmutableSet.copyOf(selectAll(new FilterOptions.Builder<ContestClarificationModel>()
                .putColumnsEq(ContestClarificationModel_.contestJid, contestJid)
                .build(), options));
    }

    @Override
    public Optional<ContestClarificationModel> selectByContestJidAndClarificationJid(
            String contestJid,
            String clarificationJid) {

        return selectByFilter(new FilterOptions.Builder<ContestClarificationModel>()
                .putColumnsEq(ContestClarificationModel_.contestJid, contestJid)
                .putColumnsEq(ContestClarificationModel_.jid, clarificationJid)
                .build());
    }

    @Override
    public long selectCountAnsweredByContestJidAndUserJid(String contestJid, String userJid) {
        return selectCount(new FilterOptions.Builder<ContestClarificationModel>()
                .putColumnsEq(ContestClarificationModel_.contestJid, contestJid)
                .putColumnsEq(ContestClarificationModel_.createdBy, userJid)
                .putColumnsEq(ContestClarificationModel_.status, ContestClarificationStatus.ANSWERED.name())
                .build());
    }

    @Override
    public long selectCountAskedByContestJid(String contestJid) {
        return selectCount(new FilterOptions.Builder<ContestClarificationModel>()
                .putColumnsEq(ContestClarificationModel_.contestJid, contestJid)
                .putColumnsEq(ContestClarificationModel_.status, ContestClarificationStatus.ASKED.name())
                .build());
    }
}
