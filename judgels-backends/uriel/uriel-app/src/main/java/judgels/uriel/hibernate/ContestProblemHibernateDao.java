package judgels.uriel.hibernate;

import static judgels.uriel.api.contest.problem.ContestProblemStatus.CLOSED;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProblemModel;
import judgels.uriel.persistence.ContestProblemModel_;

@Singleton
public class ContestProblemHibernateDao extends HibernateDao<ContestProblemModel> implements ContestProblemDao {
    @Inject
    public ContestProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ContestProblemModel> selectByContestJidAndProblemJid(String contestJid, String problemJid) {
        return selectByFilter(new FilterOptions.Builder<ContestProblemModel>()
                .putColumnsEq(ContestProblemModel_.contestJid, contestJid)
                .putColumnsEq(ContestProblemModel_.problemJid, problemJid)
                .putColumnsIn(ContestProblemModel_.status, ImmutableSet.of(OPEN.name(), CLOSED.name()))
                .build());
    }

    @Override
    public Optional<ContestProblemModel> selectByContestJidAndProblemAlias(String contestJid, String problemAlias) {
        return selectByFilter(new FilterOptions.Builder<ContestProblemModel>()
                .putColumnsEq(ContestProblemModel_.contestJid, contestJid)
                .putColumnsEq(ContestProblemModel_.alias, problemAlias)
                .putColumnsIn(ContestProblemModel_.status, ImmutableSet.of(OPEN.name(), CLOSED.name()))
                .build());
    }

    @Override
    public List<ContestProblemModel> selectAllByContestJid(String contestJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestProblemModel>()
                .putColumnsEq(ContestProblemModel_.contestJid, contestJid)
                .putColumnsIn(ContestProblemModel_.status, ImmutableSet.of(OPEN.name(), CLOSED.name()))
                .build(), options);
    }

    @Override
    public List<ContestProblemModel> selectAllOpenByContestJid(String contestJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestProblemModel>()
                .putColumnsEq(ContestProblemModel_.contestJid, contestJid)
                .putColumnsEq(ContestProblemModel_.status, OPEN.name())
                .build(), options);
    }

    @Override
    public boolean hasClosedByContestJid(String contestJid) {
        return selectCount(new FilterOptions.Builder<ContestProblemModel>()
                .putColumnsEq(ContestProblemModel_.contestJid, contestJid)
                .putColumnsEq(ContestProblemModel_.status, CLOSED.name())
                .build()) > 0;
    }
}
