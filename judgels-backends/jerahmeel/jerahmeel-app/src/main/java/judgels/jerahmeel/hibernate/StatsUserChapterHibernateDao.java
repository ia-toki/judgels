package judgels.jerahmeel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.StatsUserChapterDao;
import judgels.jerahmeel.persistence.StatsUserChapterModel;
import judgels.jerahmeel.persistence.StatsUserChapterModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class StatsUserChapterHibernateDao extends HibernateDao<StatsUserChapterModel> implements StatsUserChapterDao {
    @Inject
    public StatsUserChapterHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<StatsUserChapterModel> selectByUserJidAndChapterJid(String userJid, String chapterJid) {
        return selectByUniqueColumns(ImmutableMap.of(
                StatsUserChapterModel_.userJid, userJid,
                StatsUserChapterModel_.chapterJid, chapterJid));
    }
}
