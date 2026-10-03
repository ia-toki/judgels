package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import judgels.api.contest.module.ContestModuleType;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.ContestModuleModel;
import judgels.persistence.model.ContestModuleModel_;
import org.hibernate.Session;

public class ContestModuleDao extends Dao<ContestModuleModel> {
    @Inject
    public ContestModuleDao(DaoData data) {
        super(data);
    }

    public ContestModuleQueryBuilder selectByContestJid(String contestJid) {
        return new ContestModuleQueryBuilder(currentSession(), contestJid);
    }

    public Optional<ContestModuleModel> selectByContestJidAndType(String contestJid, ContestModuleType type) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestModuleModel_.name, type.name()))
                .unique();
    }

    public Optional<ContestModuleModel> selectEnabledByContestJidAndType(String contestJid, ContestModuleType type) {
        return selectByContestJid(contestJid)
                .whereEnabled()
                .where(columnEq(ContestModuleModel_.name, type.name()))
                .unique();
    }

    public void dump(PrintWriter output, String contestJid) {
        List<ContestModuleModel> results = selectByContestJid(contestJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_module (contestJid, name, config, enabled, createdBy, createdAt, updatedBy, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestModuleModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s, %s, %s, %s)",
                    escape(m.contestJid),
                    escape(m.name),
                    escape(m.config),
                    escape(m.enabled),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedBy),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
    }

    public static class ContestModuleQueryBuilder extends QueryBuilder<ContestModuleModel> {
        ContestModuleQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestModuleModel.class);
            where(columnEq(ContestModuleModel_.contestJid, contestJid));
        }

        public ContestModuleQueryBuilder whereEnabled() {
            where(columnEq(ContestModuleModel_.enabled, true));
            return this;
        }
    }
}
