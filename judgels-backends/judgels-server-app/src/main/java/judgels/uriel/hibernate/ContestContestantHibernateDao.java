package judgels.uriel.hibernate;

import static judgels.uriel.api.contest.contestant.ContestContestantStatus.APPROVED;

import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestContestantModel_;
import org.hibernate.Session;

public class ContestContestantHibernateDao extends HibernateDao<ContestContestantModel> implements ContestContestantDao {
    @Inject
    public ContestContestantHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestContestantHibernateQueryBuilder select() {
        return new ContestContestantHibernateQueryBuilder(currentSession());
    }

    @Override
    public ContestContestantQueryBuilder selectByContestJid(String contestJid) {
        return new ContestContestantHibernateQueryBuilder(currentSession(), contestJid);
    }

    @Override
    public Optional<ContestContestantModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestContestantModel_.userJid, userJid))
                .unique();
    }

    private static class ContestContestantHibernateQueryBuilder extends HibernateQueryBuilder<ContestContestantModel> implements ContestContestantQueryBuilder {
        ContestContestantHibernateQueryBuilder(Session currentSession) {
            super(currentSession, ContestContestantModel.class);
            where(columnEq(ContestContestantModel_.status, APPROVED.name()));
        }

        ContestContestantHibernateQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestContestantModel.class);
            where(columnEq(ContestContestantModel_.contestJid, contestJid));
            where(columnEq(ContestContestantModel_.status, APPROVED.name()));
        }

        @Override
        public ContestContestantQueryBuilder whereUserParticipated(String userJid) {
            where((cb, cq, root) -> cb.isNotNull(root.get(ContestContestantModel_.finalRank)));
            return this;
        }
    }
}
