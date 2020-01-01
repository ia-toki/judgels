package judgels.jerahmeel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.StatsUserProblemSetDao;
import judgels.jerahmeel.persistence.StatsUserProblemSetModel;
import judgels.jerahmeel.persistence.StatsUserProblemSetModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class StatsUserProblemSetHibernateDao extends HibernateDao<StatsUserProblemSetModel>
        implements StatsUserProblemSetDao {

    @Inject
    public StatsUserProblemSetHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<StatsUserProblemSetModel> selectByUserJidAndProblemSetJid(String userJid, String problemSetJid) {
        return selectByUniqueColumns(ImmutableMap.of(
                StatsUserProblemSetModel_.userJid, userJid,
                StatsUserProblemSetModel_.problemSetJid, problemSetJid));
    }
}
