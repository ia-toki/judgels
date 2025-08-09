package judgels.uriel.hibernate;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import judgels.persistence.Model_;
import judgels.persistence.api.OrderDir;
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

    @Override
    public void dump(PrintWriter output, String contestJid) {
        List<ContestAnnouncementModel> results = selectByContestJid(contestJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_announcement (jid, contestJid, title, content, status, createdBy, createdAt, updatedBy, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestAnnouncementModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    escape(m.jid),
                    escape(m.contestJid),
                    escape(m.title),
                    escape(m.content),
                    escape(m.status),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedBy),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
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
