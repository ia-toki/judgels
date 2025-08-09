package judgels.uriel.hibernate;

import static judgels.uriel.api.contest.contestant.ContestContestantStatus.APPROVED;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Model_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestContestantModel_;
import org.hibernate.Session;

public class ContestContestantHibernateDao extends HibernateDao<ContestContestantModel> implements ContestContestantDao {
    @Inject
    public ContestContestantHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestContestantHibernateQueryBuilder select() {
        return new ContestContestantHibernateQueryBuilder(currentSession());
    }

    @Override
    public ContestContestantQueryBuilder selectByContestJid(String contestJid) {
        return new ContestContestantHibernateQueryBuilder(currentSession(), contestJid);
    }

    @Override
    public Optional<ContestContestantModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestContestantModel_.userJid, userJid))
                .unique();
    }

    @Override
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

    private static class ContestContestantHibernateQueryBuilder extends HibernateQueryBuilder<ContestContestantModel> implements ContestContestantQueryBuilder {
        ContestContestantHibernateQueryBuilder(Session currentSession) {
            super(currentSession, ContestContestantModel.class);
            where(columnEq(ContestContestantModel_.status, APPROVED.name()));
        }

        ContestContestantHibernateQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestContestantModel.class);
            where(columnEq(ContestContestantModel_.contestJid, contestJid));
            where(columnEq(ContestContestantModel_.status, APPROVED.name()));
        }

        @Override
        public ContestContestantQueryBuilder whereUserParticipated(String userJid) {
            where(columnEq(ContestContestantModel_.userJid, userJid));
            where((cb, cq, root) -> cb.isNotNull(root.get(ContestContestantModel_.finalRank)));
            return this;
        }
    }
}
