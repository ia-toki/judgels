package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.ContestClarificationModel;
import judgels.persistence.model.ContestClarificationModel_;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ContestClarificationDao extends JudgelsDao<ContestClarificationModel> {
    @Inject
    public ContestClarificationDao(DaoData data) {
        super(data);
    }

    public ContestClarificationQueryBuilder selectByContestJid(String contestJid) {
        return new ContestClarificationQueryBuilder(currentSession(), contestJid);
    }

    public Optional<ContestClarificationModel> selectByContestJidAndClarificationJid(String contestJid, String clarificationJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(JudgelsModel_.jid, clarificationJid))
                .unique();
    }

    public void updateTopicJid(String oldTopicJid, String newTopicJid) {
        Query<?> query = currentSession().createQuery(
                "UPDATE uriel_contest_clarification "
                        + "SET topicJid = :newTopicJid "
                        + "WHERE topicJid = :oldTopicJid");

        query.setParameter("newTopicJid", newTopicJid);
        query.setParameter("oldTopicJid", oldTopicJid);
        query.executeUpdate();
    }

    public void dump(PrintWriter output, String contestJid) {
        List<ContestClarificationModel> results = selectByContestJid(contestJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_clarification (jid, contestJid, topicJid, title, question, answer, status, createdBy, createdAt, updatedBy, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestClarificationModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    escape(m.jid),
                    escape(m.contestJid),
                    escape(m.topicJid),
                    escape(m.title),
                    escape(m.question),
                    escape(m.answer),
                    escape(m.status),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedBy),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
    }

    public static class ContestClarificationQueryBuilder extends QueryBuilder<ContestClarificationModel> {
        ContestClarificationQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestClarificationModel.class);
            where(columnEq(ContestClarificationModel_.contestJid, contestJid));
        }

        public ContestClarificationQueryBuilder whereUserIsAsker(String userJid) {
            where(columnEq(UnmodifiableModel_.createdBy, userJid));
            return this;
        }

        public ContestClarificationQueryBuilder whereStatusIs(String status) {
            where(columnEq(ContestClarificationModel_.status, status));
            return this;
        }
    }
}
