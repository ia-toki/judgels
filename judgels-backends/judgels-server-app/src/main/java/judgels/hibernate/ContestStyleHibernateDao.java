package judgels.hibernate;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.ContestStyleDao;
import judgels.persistence.ContestStyleModel;
import judgels.persistence.ContestStyleModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class ContestStyleHibernateDao extends HibernateDao<ContestStyleModel> implements ContestStyleDao {
    @Inject
    public ContestStyleHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ContestStyleModel> selectByContestJid(String contestJid) {
        return select()
                .where(columnEq(ContestStyleModel_.contestJid, contestJid))
                .unique();
    }
}
