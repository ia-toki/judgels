package org.iatoki.judgels.jerahmeel.chapter.dependency;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

@ImplementedBy(ChapterDependencyServiceImpl.class)
public interface ChapterDependencyService {

    boolean isDependenciesFulfilled(String userJid, String chapterJid);

    boolean existsByChapterJidAndDependencyJid(String chapterJid, String dependencyJid);

    ChapterDependency findChapterDependencyById(long chapterDependencyId) throws ChapterDependencyNotFoundException;

    Page<ChapterDependency> getPageOfChapterDependencies(String chapterJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    ChapterDependency addChapterDependency(String chapterJid, String dependedChapterJid, String userJid, String userIpAddress);

    void removeChapterDependency(long chapterDependencyId);
}
