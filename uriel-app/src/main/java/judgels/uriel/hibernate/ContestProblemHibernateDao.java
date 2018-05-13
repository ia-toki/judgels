package judgels.uriel.hibernate;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProblemModel;
import judgels.uriel.persistence.ContestProblemModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestProblemHibernateDao extends HibernateDao<ContestProblemModel> implements ContestProblemDao {
    @Inject
    public ContestProblemHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<ContestProblemModel> selectByContestJidAndProblemJid(String contestJid, String problemJid) {
        return selectByFilter(new FilterOptions.Builder<ContestProblemModel>()
                .putColumnsEq(ContestProblemModel_.contestJid, contestJid)
                .putColumnsEq(ContestProblemModel_.problemJid, problemJid)
                .build());
    }

    @Override
    public List<ContestProblemModel> selectAllByContestJid(String contestJid) {
        return selectAll(new FilterOptions.Builder<ContestProblemModel>()
                .putColumnsEq(ContestProblemModel_.contestJid, contestJid)
                .build(), new SelectionOptions.Builder()
                .orderBy("alias")
                .build());
    }

    @Override
    public List<ContestProblemModel> selectAllOpenByContestJid(String contestJid) {
        return selectAll(new FilterOptions.Builder<ContestProblemModel>()
                .putColumnsEq(ContestProblemModel_.contestJid, contestJid)
                .putColumnsEq(ContestProblemModel_.status, ContestProblemStatus.OPEN.name())
                .build());
    }
}
