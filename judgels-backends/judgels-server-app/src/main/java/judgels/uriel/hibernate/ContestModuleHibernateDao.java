package judgels.uriel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
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
