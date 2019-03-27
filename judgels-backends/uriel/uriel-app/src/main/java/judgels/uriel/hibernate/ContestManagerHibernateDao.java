package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestManagerModel_;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;

@Singleton
public class ContestManagerHibernateDao extends HibernateDao<ContestManagerModel> implements ContestManagerDao {
    @Inject
    public ContestManagerHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ContestManagerModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByFilter(new FilterOptions.Builder<ContestManagerModel>()
                .putColumnsEq(ContestManagerModel_.contestJid, contestJid)
                .putColumnsEq(ContestManagerModel_.userJid, userJid)
                .build());
    }

    @Override
    public Page<ContestManagerModel> selectPagedByContestJid(String contestJid, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<ContestManagerModel>()
                .putColumnsEq(ContestManagerModel_.contestJid, contestJid)
                .build(), options);
    }

    @Override
    public Set<ContestManagerModel> selectAllByContestJid(String contestJid, SelectionOptions options) {
        return ImmutableSet.copyOf(selectAll(new FilterOptions.Builder<ContestManagerModel>()
                .putColumnsEq(ContestManagerModel_.contestJid, contestJid)
                .build(), options));
    }

    static CustomPredicateFilter<ContestModel> hasManager(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestManagerModel> sq = cq.subquery(ContestManagerModel.class);
            Root<ContestManagerModel> subRoot = sq.from(ContestManagerModel.class);

            sq.where(
                    cb.equal(subRoot.get(ContestManagerModel_.contestJid), root.get(ContestModel_.jid)),
                    cb.equal(subRoot.get(ContestManagerModel_.userJid), userJid));
            sq.select(subRoot);

            return cb.exists(sq);
        };
    }
}
