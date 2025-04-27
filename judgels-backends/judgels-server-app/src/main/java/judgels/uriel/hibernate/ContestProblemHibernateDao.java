package judgels.uriel.hibernate;

import static judgels.uriel.api.contest.problem.ContestProblemStatus.CLOSED;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;

import com.google.common.collect.ImmutableSet;
import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Model_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProblemModel;
import judgels.uriel.persistence.ContestProblemModel_;
import org.hibernate.Session;
import org.hibernate.query.Query;

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

    @Override
    public void updateProblemJid(String oldProblemJid, String newProblemJid) {
        Query<?> query = currentSession().createQuery(
                "UPDATE uriel_contest_problem "
                        + "SET problemJid = :newProblemJid "
                        + "WHERE problemJid = :oldProblemJid");

        query.setParameter("newProblemJid", newProblemJid);
        query.setParameter("oldProblemJid", oldProblemJid);
        query.executeUpdate();
    }

    @Override
    public void dump(PrintWriter output, String contestJid) {
        List<ContestProblemModel> results = selectByContestJid(contestJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_problem (contestJid, problemJid, alias, status, submissionsLimit, points, createdBy, createdAt, updatedBy, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestProblemModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    escape(m.contestJid),
                    escape(m.problemJid),
                    escape(m.alias),
                    escape(m.status),
                    escape(m.submissionsLimit),
                    escape(m.points),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedBy),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
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
