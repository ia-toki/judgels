package judgels.jerahmeel.chapter.resource;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import judgels.jerahmeel.persistence.ChapterLessonDao;
import judgels.jerahmeel.persistence.ChapterLessonModel;
import judgels.jerahmeel.persistence.ChapterLessonModel_;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.ChapterProblemModel_;
import judgels.persistence.api.OrderDir;

public class ChapterResourceStore {
    private final ChapterLessonDao lessonDao;
    private final ChapterProblemDao problemDao;

    @Inject
    public ChapterResourceStore(ChapterLessonDao lessonDao, ChapterProblemDao problemDao) {
        this.lessonDao = lessonDao;
        this.problemDao = problemDao;
    }

    public List<Optional<String>> getPreviousAndNextResourcePathsForLesson(String chapterJid, String lessonAlias) {
        List<String> paths = new ArrayList<>();
        for (ChapterLessonModel model : getLessons(chapterJid)) {
            paths.add("/lessons/" + model.alias);
        }

        Optional<String> prev = Optional.empty();
        Optional<String> next = Optional.empty();

        for (int i = 0; i < paths.size(); i++) {
            if (paths.get(i).equals("/lessons/" + lessonAlias)) {
                if (i + 1 < paths.size()) {
                    next = Optional.of(paths.get(i + 1));
                } else {
                    for (ChapterProblemModel model : getProblems(chapterJid)) {
                        next = Optional.of("/problems/" + model.alias);
                        break;
                    }
                }
                if (i > 0) {
                    prev = Optional.of(paths.get(i - 1));
                }
                break;
            }
        }
        return List.of(prev, next);
    }

    public List<Optional<String>> getPreviousAndNextResourcePathsForProblem(String chapterJid, String problemAlias) {
        List<String> paths = new ArrayList<>();
        for (ChapterProblemModel model : getProblems(chapterJid)) {
            paths.add("/problems/" + model.alias);
        }

        Optional<String> prev = Optional.empty();
        Optional<String> next = Optional.empty();

        for (int i = 0; i < paths.size(); i++) {
            if (paths.get(i).equals("/problems/" + problemAlias)) {
                if (i + 1 < paths.size()) {
                    next = Optional.of(paths.get(i + 1));
                }
                if (i > 0) {
                    prev = Optional.of(paths.get(i - 1));
                } else {
                    for (ChapterLessonModel model : getLessons(chapterJid)) {
                        prev = Optional.of("/lessons/" + model.alias);
                    }
                }
                break;
            }
        }
        return List.of(prev, next);
    }

    private List<ChapterLessonModel> getLessons(String chapterJid) {
        return lessonDao.selectByChapterJid(chapterJid).orderBy(ChapterLessonModel_.ALIAS, OrderDir.ASC).all();
    }

    private List<ChapterProblemModel> getProblems(String chapterJid) {
        return problemDao.selectByChapterJid(chapterJid).orderBy(ChapterProblemModel_.ALIAS, OrderDir.ASC).all();
    }
}
