package org.iatoki.judgels.jerahmeel.chapter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class ChapterServiceImpl implements ChapterService {

    private final ChapterDao chapterDao;

    @Inject
    public ChapterServiceImpl(ChapterDao chapterDao) {
        this.chapterDao = chapterDao;
    }

    @Override
    public boolean chapterExistsByJid(String chapterJid) {
        return chapterDao.existsByJid(chapterJid);
    }

    @Override
    public List<Chapter> getChaptersByTerm(String term) {
        List<ChapterModel> chapters = chapterDao.findSortedByFilters("id", "asc", term, 0, -1);
        ImmutableList.Builder<Chapter> chapterBuilder = ImmutableList.builder();

        for (ChapterModel chapter : chapters) {
            chapterBuilder.add(ChapterServiceUtils.createChapterFromModel(chapter));
        }

        return chapterBuilder.build();
    }

    @Override
    public Map<String, Chapter> getChaptersMapByJids(List<String> chapterJids) {
        List<ChapterModel> chapterModels = chapterDao.findSortedByFiltersIn("id", "asc", "", ImmutableMap.of(ChapterModel_.jid, chapterJids), 0, -1);
        return chapterModels.stream().collect(Collectors.toMap(m -> m.jid, m -> ChapterServiceUtils.createChapterFromModel(m)));
    }

    @Override
    public Page<Chapter> getPageOfChapters(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = chapterDao.countByFilters(filterString);
        List<ChapterModel> chapterModels = chapterDao.findSortedByFilters(orderBy, orderDir, filterString, pageIndex * pageSize, pageSize);

        List<Chapter> chapters = Lists.transform(chapterModels, m -> ChapterServiceUtils.createChapterFromModel(m));

        return new Page<>(chapters, totalPages, pageIndex, pageSize);
    }

    @Override
    public Chapter findChapterByJid(String chapterJid) {
        ChapterModel chapterModel = chapterDao.findByJid(chapterJid);

        return new Chapter(chapterModel.id, chapterModel.jid, chapterModel.name, chapterModel.description);
    }

    @Override
    public Chapter findChapterById(long chapterId) throws ChapterNotFoundException {
        ChapterModel chapterModel = chapterDao.findById(chapterId);
        if (chapterModel != null) {
            return ChapterServiceUtils.createChapterFromModel(chapterModel);
        } else {
            throw new ChapterNotFoundException("Chapter not found.");
        }
    }

    @Override
    public Map<String, String> getChapterJidToNameMapByChapterJids(Collection<String> chapterJids) {
        List<ChapterModel> chapterModels = chapterDao.findSortedByFiltersIn("id", "asc", "", ImmutableMap.of(ChapterModel_.jid, chapterJids), 0, -1);

        ImmutableMap.Builder<String, String> chapterJidToNameMap = ImmutableMap.builder();
        for (ChapterModel chapterModel : chapterModels) {
            chapterJidToNameMap.put(chapterModel.jid, chapterModel.name);
        }

        return chapterJidToNameMap.build();
    }

    @Override
    public Chapter createChapter(String name, String description, String userJid, String userIpAddress) {
        ChapterModel chapterModel = new ChapterModel();
        chapterModel.name = name;
        chapterModel.description = description;

        chapterDao.persist(chapterModel, userJid, userIpAddress);

        return ChapterServiceUtils.createChapterFromModel(chapterModel);
    }

    @Override
    public void updateChapter(String chapterJid, String name, String description, String userJid, String userIpAddress) {
        ChapterModel chapterModel = chapterDao.findByJid(chapterJid);
        chapterModel.name = name;
        chapterModel.description = description;

        chapterDao.edit(chapterModel, userJid, userIpAddress);
    }
}
