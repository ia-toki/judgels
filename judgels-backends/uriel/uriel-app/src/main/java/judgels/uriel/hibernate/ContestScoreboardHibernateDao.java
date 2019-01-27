package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.persistence.ContestScoreboardDao;
import judgels.uriel.persistence.ContestScoreboardModel;
import judgels.uriel.persistence.ContestScoreboardModel_;

@Singleton
public class ContestScoreboardHibernateDao extends HibernateDao<ContestScoreboardModel> implements
        ContestScoreboardDao {

    @Inject
    public ContestScoreboardHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ContestScoreboardModel> selectByContestJidAndType(String contestJid, ContestScoreboardType type) {
        return selectByUniqueColumns(ImmutableMap.of(
                ContestScoreboardModel_.contestJid, contestJid,
                ContestScoreboardModel_.type, type.name()));
    }
}
