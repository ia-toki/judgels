package org.iatoki.judgels.jerahmeel.chapter.dependency;

import play.data.validation.Constraints;

public final class ChapterDependencyAddForm {

    @Constraints.Required
    public String chapterJid;
}
