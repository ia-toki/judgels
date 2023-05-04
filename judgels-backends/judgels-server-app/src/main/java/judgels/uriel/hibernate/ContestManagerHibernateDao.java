package judgels.uriel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.QueryBuilder;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestManagerModel_;

public class ContestManagerHibernateDao extends HibernateDao<ContestManagerModel> implements ContestManagerDao {
    @Inject
    public ContestManagerHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public QueryBuilder<ContestManagerModel> selectByContestJid(String contestJid) {
        return new HibernateQueryBuilder<>(currentSession(), ContestManagerModel.class)
                .where(columnEq(ContestManagerModel_.contestJid, contestJid));
    }

    @Override
    public Optional<ContestManagerModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestManagerModel_.userJid, userJid))
                .unique();
    }
}
