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
import judgels.persistence.model.ContestManagerModel;
import judgels.persistence.model.ContestManagerModel_;

public class ContestManagerDao extends Dao<ContestManagerModel> {
    @Inject
    public ContestManagerDao(DaoData data) {
        super(data);
    }

    public QueryBuilder<ContestManagerModel> selectByContestJid(String contestJid) {
        return new QueryBuilder<>(currentSession(), ContestManagerModel.class)
                .where(columnEq(ContestManagerModel_.contestJid, contestJid));
    }

    public Optional<ContestManagerModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestManagerModel_.userJid, userJid))
                .unique();
    }

    public void dump(PrintWriter output, String contestJid) {
        List<ContestManagerModel> results = selectByContestJid(contestJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_manager (contestJid, userJid, createdBy, createdAt, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestManagerModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s)",
                    escape(m.contestJid),
                    escape(m.userJid),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
    }
}
