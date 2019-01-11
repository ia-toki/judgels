package org.iatoki.judgels.jerahmeel.chapter.problem;

final class ChapterProblemServiceUtils {

    private ChapterProblemServiceUtils() {
        // prevent instantiation
    }

    static ChapterProblem createFromModel(ChapterProblemModel model) {
        return new ChapterProblem(model.id, model.chapterJid, model.problemJid, model.problemSecret, model.alias, ChapterProblemType.valueOf(model.type), ChapterProblemStatus.valueOf(model.status));
    }
}
