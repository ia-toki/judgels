package judgels.uriel.hibernate;

import java.time.Clock;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.persistence.ContestClarificationModel;
import judgels.uriel.persistence.ContestClarificationModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestClarificationHibernateDao extends JudgelsHibernateDao<ContestClarificationModel> implements
        ContestClarificationDao {

    @Inject
    public ContestClarificationHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public List<ContestClarificationModel> selectAllByContestJidAndUserJid(String contestJid, String userJid) {
        return selectAll(new FilterOptions.Builder<ContestClarificationModel>()
                .putColumnsEq(ContestClarificationModel_.contestJid, contestJid)
                .putColumnsEq(ContestClarificationModel_.createdBy, userJid)
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
}
