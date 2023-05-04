package judgels.uriel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.uriel.persistence.ContestStyleDao;
import judgels.uriel.persistence.ContestStyleModel;
import judgels.uriel.persistence.ContestStyleModel_;

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
