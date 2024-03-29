package judgels.uriel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.persistence.ContestScoreboardDao;
import judgels.uriel.persistence.ContestScoreboardModel;
import judgels.uriel.persistence.ContestScoreboardModel_;

public class ContestScoreboardHibernateDao extends HibernateDao<ContestScoreboardModel> implements ContestScoreboardDao {
    @Inject
    public ContestScoreboardHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ContestScoreboardModel> selectByContestJidAndType(String contestJid, ContestScoreboardType type) {
        return select()
                .where(columnEq(ContestScoreboardModel_.contestJid, contestJid))
                .where(columnEq(ContestScoreboardModel_.type, type.name()))
                .unique();
    }
}
