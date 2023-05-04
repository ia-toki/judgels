package judgels.uriel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.persistence.ContestClarificationModel;
import judgels.uriel.persistence.ContestClarificationModel_;
import org.hibernate.Session;

public class ContestClarificationHibernateDao extends JudgelsHibernateDao<ContestClarificationModel> implements ContestClarificationDao {
    @Inject
    public ContestClarificationHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestClarificationQueryBuilder selectByContestJid(String contestJid) {
        return new ContestClarificationHibernateQueryBuilder(currentSession(), contestJid);
    }

    @Override
    public Optional<ContestClarificationModel> selectByContestJidAndClarificationJid(String contestJid, String clarificationJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(JudgelsModel_.jid, clarificationJid))
                .unique();
    }

    private static class ContestClarificationHibernateQueryBuilder extends HibernateQueryBuilder<ContestClarificationModel> implements ContestClarificationQueryBuilder {
        ContestClarificationHibernateQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestClarificationModel.class);
            where(columnEq(ContestClarificationModel_.contestJid, contestJid));
        }

        @Override
        public ContestClarificationQueryBuilder whereUserIsAsker(String userJid) {
            where(columnEq(UnmodifiableModel_.createdBy, userJid));
            return this;
        }

        @Override
        public ContestClarificationQueryBuilder whereStatusIs(String status) {
            where(columnEq(ContestClarificationModel_.status, status));
            return this;
        }
    }
}
