package judgels.persistence.dao;

import static judgels.api.contest.contestant.ContestContestantStatus.APPROVED;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.ContestContestantModel;
import judgels.persistence.model.ContestContestantModel_;
import org.hibernate.Session;

public class ContestContestantDao extends Dao<ContestContestantModel> {
    @Inject
    public ContestContestantDao(DaoData data) {
        super(data);
    }

    @Override
    public ContestContestantQueryBuilder select() {
        return new ContestContestantQueryBuilder(currentSession());
    }

    public ContestContestantQueryBuilder selectByContestJid(String contestJid) {
        return new ContestContestantQueryBuilder(currentSession(), contestJid);
    }

    public Optional<ContestContestantModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestContestantModel_.userJid, userJid))
                .unique();
    }

    public void dump(PrintWriter output, String contestJid) {
        List<ContestContestantModel> results = selectByContestJid(contestJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_contestant (contestJid, userJid, status, contestStartTime, finalRank, createdBy, createdAt, updatedBy, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestContestantModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    escape(m.contestJid),
                    escape(m.userJid),
                    escape(m.status),
                    escape(m.contestStartTime),
                    escape(m.finalRank),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedBy),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
    }

    public static class ContestContestantQueryBuilder extends QueryBuilder<ContestContestantModel> {
        ContestContestantQueryBuilder(Session currentSession) {
            super(currentSession, ContestContestantModel.class);
            where(columnEq(ContestContestantModel_.status, APPROVED.name()));
        }

        ContestContestantQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestContestantModel.class);
            where(columnEq(ContestContestantModel_.contestJid, contestJid));
            where(columnEq(ContestContestantModel_.status, APPROVED.name()));
        }

        public ContestContestantQueryBuilder whereUserParticipated(String userJid) {
            where(columnEq(ContestContestantModel_.userJid, userJid));
            where((cb, cq, root) -> cb.isNotNull(root.get(ContestContestantModel_.finalRank)));
            return this;
        }
    }
}
