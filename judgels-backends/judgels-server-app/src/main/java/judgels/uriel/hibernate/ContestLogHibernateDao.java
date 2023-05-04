package judgels.uriel.hibernate;

import javax.inject.Inject;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestLogModel;
import judgels.uriel.persistence.ContestLogModel_;
import org.hibernate.Session;

public class ContestLogHibernateDao extends UnmodifiableHibernateDao<ContestLogModel> implements ContestLogDao {
    @Inject
    public ContestLogHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestLogQueryBuilder selectByContestJid(String contestJid) {
        return new ContestLogHibernateQueryBuilder(currentSession(), contestJid);
    }

    private static class ContestLogHibernateQueryBuilder extends HibernateQueryBuilder<ContestLogModel> implements ContestLogQueryBuilder {
        ContestLogHibernateQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestLogModel.class);
            where(columnEq(ContestLogModel_.contestJid, contestJid));
        }

        @Override
        public ContestLogQueryBuilder whereUserIs(String userJid) {
            where(columnEq(UnmodifiableModel_.createdBy, userJid));
            return this;
        }

        @Override
        public ContestLogQueryBuilder whereProblemIs(String problemJid) {
            where(columnEq(ContestLogModel_.problemJid, problemJid));
            return this;
        }
    }
}
