package judgels.uriel.hibernate;

import static judgels.uriel.api.contest.problem.ContestProblemStatus.CLOSED;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProblemModel;
import judgels.uriel.persistence.ContestProblemModel_;
import org.hibernate.Session;

public class ContestProblemHibernateDao extends HibernateDao<ContestProblemModel> implements ContestProblemDao {
    @Inject
    public ContestProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestProblemQueryBuilder selectByContestJid(String contestJid) {
        return new ContestProblemHibernateQueryBuilder(currentSession(), contestJid);
    }

    @Override
    public Optional<ContestProblemModel> selectByContestJidAndProblemJid(String contestJid, String problemJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestProblemModel_.problemJid, problemJid))
                .unique();
    }

    @Override
    public Optional<ContestProblemModel> selectByContestJidAndProblemAlias(String contestJid, String problemAlias) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestProblemModel_.alias, problemAlias))
                .unique();
    }

    private static class ContestProblemHibernateQueryBuilder extends HibernateQueryBuilder<ContestProblemModel> implements ContestProblemQueryBuilder {
        ContestProblemHibernateQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestProblemModel.class);
            where(columnEq(ContestProblemModel_.contestJid, contestJid));
            where(columnIn(ContestProblemModel_.status, ImmutableSet.of(OPEN.name(), CLOSED.name())));
        }

        @Override
        public ContestProblemHibernateQueryBuilder orderBy(String column, OrderDir dir) {
            super.orderBy(column, dir);
            return this;
        }

        @Override
        public ContestProblemQueryBuilder whereStatusIs(String status) {
            where(columnEq(ContestProblemModel_.status, status));
            return this;
        }
    }
}
