package judgels.uriel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.uriel.persistence.ContestStyleDao;
import judgels.uriel.persistence.ContestStyleModel;
import judgels.uriel.persistence.ContestStyleModel_;

@Singleton
public class ContestStyleHibernateDao extends HibernateDao<ContestStyleModel> implements ContestStyleDao {
    @Inject
    public ContestStyleHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ContestStyleModel> selectByContestJid(String contestJid) {
        return selectByFilter(new FilterOptions.Builder<ContestStyleModel>()
                .putColumnsEq(ContestStyleModel_.contestJid, contestJid)
                .build());
    }
}
