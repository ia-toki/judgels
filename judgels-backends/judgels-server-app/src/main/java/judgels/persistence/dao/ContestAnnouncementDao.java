package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.ContestAnnouncementModel;
import judgels.persistence.model.ContestAnnouncementModel_;
import org.hibernate.Session;

public class ContestAnnouncementDao extends JudgelsDao<ContestAnnouncementModel> {
    @Inject
    public ContestAnnouncementDao(DaoData data) {
        super(data);
    }

    public ContestAnnouncementQueryBuilder selectByContestJid(String contestJid) {
        return new ContestAnnouncementQueryBuilder(currentSession(), contestJid);
    }

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

    public static class ContestAnnouncementQueryBuilder extends QueryBuilder<ContestAnnouncementModel> {
        ContestAnnouncementQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestAnnouncementModel.class);
            where(columnEq(ContestAnnouncementModel_.contestJid, contestJid));
        }

        public ContestAnnouncementQueryBuilder whereStatusIs(String status) {
            where(columnEq(ContestAnnouncementModel_.status, status));
            return this;
        }
    }
}
