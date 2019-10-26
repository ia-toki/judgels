package org.iatoki.judgels.jerahmeel.chapter.problem;

import judgels.jerahmeel.persistence.ChapterProblemModel;

final class ChapterProblemServiceUtils {

    private ChapterProblemServiceUtils() {
        // prevent instantiation
    }

    static ChapterProblem createFromModel(ChapterProblemModel model) {
        return new ChapterProblem(model.id, model.chapterJid, model.problemJid, model.alias, ChapterProblemType.valueOf(model.type), ChapterProblemStatus.valueOf(model.status));
    }
}
