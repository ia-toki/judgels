package judgels.uriel.hibernate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.uriel.persistence.ContestGroupContestDao;
import judgels.uriel.persistence.ContestGroupContestModel;
import judgels.uriel.persistence.ContestGroupContestModel_;

@Singleton
public class ContestGroupContestHibernateDao extends HibernateDao<ContestGroupContestModel>
        implements ContestGroupContestDao {

    @Inject
    public ContestGroupContestHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<ContestGroupContestModel> selectAllByContestGroupJid(String contestGroupJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestGroupContestModel>()
                .putColumnsEq(ContestGroupContestModel_.contestGroupJid, contestGroupJid)
                .build(), options);
    }

    @Override
    public Set<String> selectAllContestGroupJidsByContestJids(Set<String> contestJids) {
        return selectAll(new FilterOptions.Builder<ContestGroupContestModel>()
                .putColumnsIn(ContestGroupContestModel_.contestJid, contestJids)
                .build())
                .stream()
                .map(m -> m.contestGroupJid)
                .collect(Collectors.toSet());
    }
}
