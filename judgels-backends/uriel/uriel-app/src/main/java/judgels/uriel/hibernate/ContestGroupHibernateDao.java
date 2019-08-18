package judgels.uriel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestGroupDao;
import judgels.uriel.persistence.ContestGroupModel;
import judgels.uriel.persistence.ContestGroupModel_;

@Singleton
public class ContestGroupHibernateDao extends JudgelsHibernateDao<ContestGroupModel> implements ContestGroupDao {
    @Inject
    public ContestGroupHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ContestGroupModel> selectBySlug(String contestGroupSlug) {
        return selectByFilter(new FilterOptions.Builder<ContestGroupModel>()
                .putColumnsEq(ContestGroupModel_.slug, contestGroupSlug)
                .build());
    }
}
