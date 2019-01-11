package org.iatoki.judgels.jerahmeel.chapter.problem;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

import java.util.Map;

@ImplementedBy(ChapterProblemServiceImpl.class)
public interface ChapterProblemService {

    boolean aliasExistsInChapter(String chapterJid, String alias);

    ChapterProblem findChapterProblemById(long chapterProblemId) throws ChapterProblemNotFoundException;

    Page<ChapterProblem> getPageOfChapterProblems(String chapterJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Page<ChapterProblemWithProgress> getPageOfChapterProblemsWithProgress(String userJid, String chapterJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    void addChapterProblem(String chapterJid, String problemJid, String problemSecret, String alias, ChapterProblemType type, ChapterProblemStatus status, String userJid, String userIpAddress);

    void updateChapterProblem(long chapterProblemId, String alias, ChapterProblemStatus status, String userJid, String userIpAddress);

    void removeChapterProblem(long chapterProblemId);

    Map<String, String> getProgrammingProblemJidToAliasMapByChapterJid(String chapterJid);

    Map<String, String> getBundleProblemJidToAliasMapByChapterJid(String chapterJid);

    ChapterProblem findChapterProblemByChapterJidAndProblemJid(String chapterJid, String problemJid);
}
