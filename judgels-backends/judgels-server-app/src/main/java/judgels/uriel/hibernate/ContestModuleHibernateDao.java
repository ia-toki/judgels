package judgels.uriel.hibernate;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.Model_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestModuleModel_;
import org.hibernate.Session;

public class ContestModuleHibernateDao extends HibernateDao<ContestModuleModel> implements ContestModuleDao {
    @Inject
    public ContestModuleHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestModuleQueryBuilder selectByContestJid(String contestJid) {
        return new ContestModuleHibernateQueryBuilder(currentSession(), contestJid);
    }

    @Override
    public Optional<ContestModuleModel> selectByContestJidAndType(String contestJid, ContestModuleType type) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestModuleModel_.name, type.name()))
                .unique();
    }

    @Override
    public Optional<ContestModuleModel> selectEnabledByContestJidAndType(String contestJid, ContestModuleType type) {
        return selectByContestJid(contestJid)
                .whereEnabled()
                .where(columnEq(ContestModuleModel_.name, type.name()))
                .unique();
    }

    @Override
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

    private static class ContestModuleHibernateQueryBuilder extends HibernateQueryBuilder<ContestModuleModel> implements ContestModuleQueryBuilder {
        ContestModuleHibernateQueryBuilder(Session currentSession, String contestJid) {
            super(currentSession, ContestModuleModel.class);
            where(columnEq(ContestModuleModel_.contestJid, contestJid));
        }

        @Override
        public ContestModuleQueryBuilder whereEnabled() {
            where(columnEq(ContestModuleModel_.enabled, true));
            return this;
        }
    }
}
