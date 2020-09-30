package judgels.jerahmeel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.StatsUserChapterDao;
import judgels.jerahmeel.persistence.StatsUserChapterModel;
import judgels.jerahmeel.persistence.StatsUserChapterModel_;
import judgels.persistence.FilterOptions;
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

    @Override
    public List<StatsUserChapterModel> selectAllByUserJidAndChapterJids(String userJid, Set<String> chapterJids) {
        return selectAll(new FilterOptions.Builder<StatsUserChapterModel>()
                .putColumnsEq(StatsUserChapterModel_.userJid, userJid)
                .putColumnsIn(StatsUserChapterModel_.chapterJid, chapterJids)
                .build());
    }

    @Override
    public List<StatsUserChapterModel> selectAllByUserJidsAndChapterJids(
            Set<String> userJids,
            Set<String> chapterJids) {

        return selectAll(new FilterOptions.Builder<StatsUserChapterModel>()
                .putColumnsIn(StatsUserChapterModel_.userJid, userJids)
                .putColumnsIn(StatsUserChapterModel_.chapterJid, chapterJids)
                .build());
    }
}
