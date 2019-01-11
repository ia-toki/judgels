package org.iatoki.judgels.jerahmeel.chapter;

final class ChapterServiceUtils {

    private ChapterServiceUtils() {
        // prevent instantiation
    }

    static Chapter createChapterFromModel(ChapterModel chapterModel) {
        return new Chapter(chapterModel.id, chapterModel.jid, chapterModel.name, chapterModel.description);
    }
}
