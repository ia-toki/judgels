package judgels.hibernate;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import judgels.api.contest.scoreboard.ContestScoreboardType;
import judgels.persistence.ContestScoreboardDao;
import judgels.persistence.ContestScoreboardModel;
import judgels.persistence.ContestScoreboardModel_;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class ContestScoreboardHibernateDao extends HibernateDao<ContestScoreboardModel> implements ContestScoreboardDao {
    @Inject
    public ContestScoreboardHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public QueryBuilder<ContestScoreboardModel> selectByContestJid(String contestJid) {
        return select()
                .where(columnEq(ContestScoreboardModel_.contestJid, contestJid));
    }

    @Override
    public Optional<ContestScoreboardModel> selectByContestJidAndType(String contestJid, ContestScoreboardType type) {
        return select()
                .where(columnEq(ContestScoreboardModel_.contestJid, contestJid))
                .where(columnEq(ContestScoreboardModel_.type, type.name()))
                .unique();
    }

    @Override
    public void dump(PrintWriter output, String contestJid) {
        List<ContestScoreboardModel> results = selectByContestJid(contestJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_scoreboard (contestJid, type, scoreboard, createdBy, createdAt, updatedBy, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestScoreboardModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s, %s, %s)",
                    escape(m.contestJid),
                    escape(m.type),
                    escape(m.scoreboard),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedBy),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
    }
}
