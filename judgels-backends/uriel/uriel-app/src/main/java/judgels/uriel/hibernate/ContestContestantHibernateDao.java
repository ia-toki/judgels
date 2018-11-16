package judgels.uriel.hibernate;

import static judgels.uriel.api.contest.contestant.ContestContestantStatus.APPROVED;

import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.ActorProvider;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestContestantModel_;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestContestantHibernateDao extends HibernateDao<ContestContestantModel> implements
        ContestContestantDao {

    @Inject
    public ContestContestantHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<ContestContestantModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByFilter(new FilterOptions.Builder<ContestContestantModel>()
                .putColumnsEq(ContestContestantModel_.contestJid, contestJid)
                .putColumnsEq(ContestContestantModel_.userJid, userJid)
                .putColumnsEq(ContestContestantModel_.status, APPROVED.name())
                .build());
    }

    @Override
    public long selectCountApprovedByContestJid(String contestJid) {
        return selectCount(new FilterOptions.Builder<ContestContestantModel>()
                .putColumnsEq(ContestContestantModel_.contestJid, contestJid)
                .putColumnsEq(ContestContestantModel_.status, APPROVED.name())
                .build());
    }

    @Override
    public Set<ContestContestantModel> selectAllByContestJid(String contestJid) {
        return ImmutableSet.copyOf(selectAll(new FilterOptions.Builder<ContestContestantModel>()
                .putColumnsEq(ContestContestantModel_.contestJid, contestJid)
                .build()));
    }

    @Override
    public Set<ContestContestantModel> selectAllApprovedByContestJid(String contestJid) {
        return ImmutableSet.copyOf(selectAll(new FilterOptions.Builder<ContestContestantModel>()
                .putColumnsEq(ContestContestantModel_.contestJid, contestJid)
                .putColumnsEq(ContestContestantModel_.status, APPROVED.name())
                .build()));
    }

    static CustomPredicateFilter<ContestModel> hasContestant(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestContestantModel> sq = cq.subquery(ContestContestantModel.class);
            Root<ContestContestantModel> subRoot = sq.from(ContestContestantModel.class);

            sq.where(
                    cb.equal(subRoot.get(ContestContestantModel_.contestJid), root.get(ContestModel_.jid)),
                    cb.equal(subRoot.get(ContestContestantModel_.userJid), userJid),
                    cb.equal(subRoot.get(ContestContestantModel_.status), APPROVED.name()));
            sq.select(subRoot);

            return cb.exists(sq);
        };
    }
}
