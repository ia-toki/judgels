package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.persistence.ContestGroupScoreboardDao;
import judgels.uriel.persistence.ContestGroupScoreboardModel;
import judgels.uriel.persistence.ContestGroupScoreboardModel_;

@Singleton
public class ContestGroupScoreboardHibernateDao extends HibernateDao<ContestGroupScoreboardModel> implements
        ContestGroupScoreboardDao {

    @Inject
    public ContestGroupScoreboardHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ContestGroupScoreboardModel> selectByContestGroupJidAndType(
            String contestGroupJid,
            ContestScoreboardType type) {

        return selectByUniqueColumns(ImmutableMap.of(
                ContestGroupScoreboardModel_.contestGroupJid, contestGroupJid,
                ContestGroupScoreboardModel_.type, type.name()));
    }
}
