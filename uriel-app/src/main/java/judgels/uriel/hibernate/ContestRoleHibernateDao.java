package judgels.uriel.hibernate;

import static judgels.persistence.CustomPredicateFilter.or;
import static judgels.uriel.hibernate.ContestContestantHibernateDao.hasContestant;
import static judgels.uriel.hibernate.ContestHibernateDao.hasContestJid;
import static judgels.uriel.hibernate.ContestManagerHibernateDao.hasManager;
import static judgels.uriel.hibernate.ContestModuleHibernateDao.hasModule;
import static judgels.uriel.hibernate.ContestSupervisorHibernateDao.hasSupervisor;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestRoleDao;
import org.hibernate.SessionFactory;

@Singleton
public class ContestRoleHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestRoleDao {
    @Inject
    public ContestRoleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean isViewerOrAbove(String userJid, String contestJid) {
        return selectByFilter(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasContestJid(contestJid))
                .addCustomPredicates(hasViewerOrAbove(userJid))
                .build()).isPresent();
    }

    @Override
    public boolean isContestantOrAbove(String userJid, String contestJid) {
        return selectByFilter(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasContestJid(contestJid))
                .addCustomPredicates(hasContestantOrAbove(userJid))
                .build()).isPresent();
    }

    @Override
    public boolean isManager(String userJid, String contestJid) {
        return selectByFilter(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasContestJid(contestJid))
                .addCustomPredicates(hasManager(userJid))
                .build()).isPresent();
    }

    static CustomPredicateFilter<ContestModel> hasViewerOrAbove(String userJid) {
        return or(
                hasModule(ContestModuleType.REGISTRATION),
                hasContestant(userJid),
                hasSupervisor(userJid),
                hasManager(userJid));
    }

    static CustomPredicateFilter<ContestModel> hasContestantOrAbove(String userJid) {
        return or(
                hasContestant(userJid),
                hasSupervisor(userJid),
                hasManager(userJid));
    }
}
