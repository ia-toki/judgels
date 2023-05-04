package judgels.uriel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.QueryBuilder;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.uriel.persistence.ContestSupervisorDao;
import judgels.uriel.persistence.ContestSupervisorModel;
import judgels.uriel.persistence.ContestSupervisorModel_;

public class ContestSupervisorHibernateDao extends HibernateDao<ContestSupervisorModel> implements ContestSupervisorDao {
    @Inject
    public ContestSupervisorHibernateDao(HibernateDaoData data) {
        super(data);
    }


    @Override
    public QueryBuilder<ContestSupervisorModel> selectByContestJid(String contestJid) {
        return new HibernateQueryBuilder<>(currentSession(), ContestSupervisorModel.class)
                .where(columnEq(ContestSupervisorModel_.contestJid, contestJid));
    }

    @Override
    public Optional<ContestSupervisorModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestSupervisorModel_.userJid, userJid))
                .unique();
    }
}
