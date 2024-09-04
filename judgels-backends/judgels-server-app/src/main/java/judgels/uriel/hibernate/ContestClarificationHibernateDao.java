package judgels.uriel.hibernate;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.Model_;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
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

    @Override
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
