package judgels.jerahmeel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.jerahmeel.persistence.StatsUserProblemModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class StatsUserProblemHibernateDao extends HibernateDao<StatsUserProblemModel> implements StatsUserProblemDao {
    @Inject
    public StatsUserProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<StatsUserProblemModel> selectByUserJidAndProblemJid(String userJid, String problemJid) {
        return selectByUniqueColumns(ImmutableMap.of(
                StatsUserProblemModel_.userJid, userJid,
                StatsUserProblemModel_.problemJid, problemJid));
    }
}
