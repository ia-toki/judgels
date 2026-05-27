package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.dao.ContestStyleDao;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.model.ContestStyleModel;
import judgels.persistence.model.ContestStyleModel_;

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
