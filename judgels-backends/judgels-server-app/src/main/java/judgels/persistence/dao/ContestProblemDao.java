package judgels.persistence.dao;

import static judgels.api.contest.problem.ContestProblemStatus.CLOSED;
import static judgels.api.contest.problem.ContestProblemStatus.OPEN;

import com.google.common.collect.ImmutableSet;
import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.ContestProblemModel;
import judgels.persistence.model.ContestProblemModel_;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ContestProblemDao extends Dao<ContestProblemModel> {
    @Inject
    public ContestProblemDao(DaoData data) {
        super(data);
    }

    public ContestProblemQueryBuilder selectByContestJid(String contestJid) {
        return new ContestProblemQueryBuilder(currentSession(), contestJid);
    }

    public Optional<ContestProblemModel> selectByContestJidAndProblemJid(String contestJid, String problemJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestProblemModel_.problemJid, problemJid))
                .unique();
    }

    public Optional<ContestProblemModel> selectByContestJidAndProblemAlias(String contestJid, String problemAlias) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestProblemModel_.alias, problemAlias))
                .unique();
    }

    public void updateProblemJid(String oldProblemJid, String newProblemJid) {
        Query<?> query = currentSession().createQuery(
                "UPDATE uriel_contest_problem "
                        + "SET problemJid = :newProblemJid "
                        + "WHERE problemJid = :oldProblemJid");

        query.setParameter("newProblemJid", newProblemJid);
        query.setParameter("oldProblemJid", oldProblemJid);
        query.executeUpdate();
    }

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

    public static class ContestProblemQueryBuilder extends QueryBuilder<ContestProblemModel> {
        ContestProblemQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestProblemModel.class);
            where(columnEq(ContestProblemModel_.contestJid, contestJid));
            where(columnIn(ContestProblemModel_.status, ImmutableSet.of(OPEN.name(), CLOSED.name())));
        }

        @Override
        public ContestProblemQueryBuilder orderBy(String column, OrderDir dir) {
            super.orderBy(column, dir);
            return this;
        }

        public ContestProblemQueryBuilder whereStatusIs(String status) {
            where(columnEq(ContestProblemModel_.status, status));
            return this;
        }
    }
}
