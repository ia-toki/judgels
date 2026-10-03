package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.ContestSupervisorModel;
import judgels.persistence.model.ContestSupervisorModel_;

public class ContestSupervisorDao extends Dao<ContestSupervisorModel> {
    @Inject
    public ContestSupervisorDao(DaoData data) {
        super(data);
    }


    public QueryBuilder<ContestSupervisorModel> selectByContestJid(String contestJid) {
        return new QueryBuilder<>(currentSession(), ContestSupervisorModel.class)
                .where(columnEq(ContestSupervisorModel_.contestJid, contestJid));
    }

    public Optional<ContestSupervisorModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestSupervisorModel_.userJid, userJid))
                .unique();
    }

    public void dump(PrintWriter output, String contestJid) {
        List<ContestSupervisorModel> results = selectByContestJid(contestJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_supervisor (contestJid, userJid, permission, createdBy, createdAt, updatedBy, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestSupervisorModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s, %s, %s)",
                    escape(m.contestJid),
                    escape(m.userJid),
                    escape(m.permission),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedBy),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
    }
}
