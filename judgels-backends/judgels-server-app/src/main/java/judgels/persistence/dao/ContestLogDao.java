package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import judgels.persistence.DaoData;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.ContestLogModel;
import judgels.persistence.model.ContestLogModel_;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ContestLogDao extends UnmodifiableDao<ContestLogModel> {
    @Inject
    public ContestLogDao(DaoData data) {
        super(data);
    }

    public ContestLogQueryBuilder selectByContestJid(String contestJid) {
        return new ContestLogQueryBuilder(currentSession(), contestJid);
    }

    public void updateProblemJid(String oldProblemJid, String newProblemJid) {
        Query<?> query = currentSession().createQuery(
                "UPDATE uriel_contest_log "
                        + "SET problemJid = :newProblemJid "
                        + "WHERE problemJid = :oldProblemJid");

        query.setParameter("newProblemJid", newProblemJid);
        query.setParameter("oldProblemJid", oldProblemJid);
        query.executeUpdate();
    }

    public void dump(PrintWriter output, String contestJid) {
        List<ContestLogModel> results = selectByContestJid(contestJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_log (contestJid, event, object, problemJid, createdBy, createdAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestLogModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s, %s)",
                    escape(m.contestJid),
                    escape(m.event),
                    escape(m.object),
                    escape(m.problemJid),
                    escape(m.createdBy),
                    escape(m.createdAt)));
        }
        output.write(";\n");
    }

    public static class ContestLogQueryBuilder extends QueryBuilder<ContestLogModel> {
        ContestLogQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestLogModel.class);
            where(columnEq(ContestLogModel_.contestJid, contestJid));
        }

        public ContestLogQueryBuilder whereUserIs(String userJid) {
            where(columnEq(UnmodifiableModel_.createdBy, userJid));
            return this;
        }

        public ContestLogQueryBuilder whereProblemIs(String problemJid) {
            where(columnEq(ContestLogModel_.problemJid, problemJid));
            return this;
        }
    }
}
