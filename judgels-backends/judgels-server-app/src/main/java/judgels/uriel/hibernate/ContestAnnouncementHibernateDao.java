package judgels.uriel.hibernate;

import javax.inject.Inject;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestAnnouncementModel;
import judgels.uriel.persistence.ContestAnnouncementModel_;
import org.hibernate.Session;

public class ContestAnnouncementHibernateDao extends JudgelsHibernateDao<ContestAnnouncementModel> implements ContestAnnouncementDao {
    @Inject
    public ContestAnnouncementHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestAnnouncementQueryBuilder selectByContestJid(String contestJid) {
        return new ContestAnnouncementHibernateQueryBuilder(currentSession(), contestJid);
    }

    private static class ContestAnnouncementHibernateQueryBuilder extends HibernateQueryBuilder<ContestAnnouncementModel> implements ContestAnnouncementQueryBuilder {
        ContestAnnouncementHibernateQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestAnnouncementModel.class);
            where(columnEq(ContestAnnouncementModel_.contestJid, contestJid));
        }

        @Override
        public ContestAnnouncementQueryBuilder whereStatusIs(String status) {
            where(columnEq(ContestAnnouncementModel_.status, status));
            return this;
        }
    }
}
