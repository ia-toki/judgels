package org.iatoki.judgels.sandalphon.lesson;

import play.data.validation.Constraints;

public class LessonCreateForm {

    @Constraints.Required
    @Constraints.Pattern("^[a-z0-9]+(-[a-z0-9]+)*$")
    public String slug;

    public String additionalNote;

    @Constraints.Required
    public String initLanguageCode;
}
