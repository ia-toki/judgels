package org.iatoki.judgels.jerahmeel.chapter.dependency;

import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.jerahmeel.chapter.ChapterDao;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class ChapterDependencyServiceImpl implements ChapterDependencyService {

    private final ChapterDao chapterDao;
    private final ChapterDependencyDao chapterDependencyDao;

    @Inject
    public ChapterDependencyServiceImpl(ChapterDao chapterDao, ChapterDependencyDao chapterDependencyDao) {
        this.chapterDao = chapterDao;
        this.chapterDependencyDao = chapterDependencyDao;
    }

    @Override
    public boolean isDependenciesFulfilled(String userJid, String chapterJid) {
        // We disable locking feature
        return true;
    }

    @Override
    public boolean existsByChapterJidAndDependencyJid(String chapterJid, String dependencyJid) {
        return chapterDependencyDao.existsByChapterJidAndDependencyJid(chapterJid, dependencyJid);
    }

    @Override
    public ChapterDependency findChapterDependencyById(long chapterDependencyId) throws ChapterDependencyNotFoundException {
        ChapterDependencyModel chapterDependencyModel = chapterDependencyDao.findById(chapterDependencyId);
        if (chapterDependencyModel != null) {
            return new ChapterDependency(chapterDependencyModel.id, chapterDependencyModel.chapterJid, chapterDependencyModel.dependedChapterJid, null);
        } else {
            throw new ChapterDependencyNotFoundException("Chapter Dependency Not Found.");
        }
    }

    @Override
    public Page<ChapterDependency> getPageOfChapterDependencies(String chapterJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = chapterDependencyDao.countByFiltersEq(filterString, ImmutableMap.of(ChapterDependencyModel_.chapterJid, chapterJid));
        List<ChapterDependencyModel> chapterDependencyModels = chapterDependencyDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(ChapterDependencyModel_.chapterJid, chapterJid), pageIndex * pageSize, pageSize);

        List<ChapterDependency> chapterDependencies = chapterDependencyModels.stream().map(s -> new ChapterDependency(s.id, s.chapterJid, s.dependedChapterJid, chapterDao.findByJid(s.dependedChapterJid).name)).collect(Collectors.toList());

        return new Page<>(chapterDependencies, totalPages, pageIndex, pageSize);
    }

    @Override
    public ChapterDependency addChapterDependency(String chapterJid, String dependedChapterJid, String userJid, String userIpAddress) {
        ChapterDependencyModel chapterDependencyModel = new ChapterDependencyModel();
        chapterDependencyModel.chapterJid = chapterJid;
        chapterDependencyModel.dependedChapterJid = dependedChapterJid;

        chapterDependencyDao.persist(chapterDependencyModel, userJid, userIpAddress);

        return new ChapterDependency(chapterDependencyModel.id, chapterDependencyModel.chapterJid, chapterDependencyModel.dependedChapterJid, null);
    }

    @Override
    public void removeChapterDependency(long chapterDependencyId) {
        ChapterDependencyModel chapterDependencyModel = chapterDependencyDao.findById(chapterDependencyId);

        chapterDependencyDao.remove(chapterDependencyModel);
    }
}
