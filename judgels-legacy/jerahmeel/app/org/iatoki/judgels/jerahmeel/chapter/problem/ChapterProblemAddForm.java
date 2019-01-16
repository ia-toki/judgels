package org.iatoki.judgels.jerahmeel.chapter.problem;

import play.data.validation.Constraints;

public final class ChapterProblemAddForm {

    @Constraints.Required
    public String alias;

    @Constraints.Required
    public String problemSlug;

    @Constraints.Required
    public String type;

    @Constraints.Required
    public String status;
}
