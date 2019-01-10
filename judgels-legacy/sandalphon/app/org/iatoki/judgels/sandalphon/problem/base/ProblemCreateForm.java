package org.iatoki.judgels.sandalphon.problem.base;

import play.data.validation.Constraints;

public class ProblemCreateForm {

    @Constraints.Required
    public String type;

    @Constraints.Required
    @Constraints.Pattern("^[a-z0-9]+(-[a-z0-9]+)*$")
    public String slug;

    public String additionalNote;

    @Constraints.Required
    public String initLanguageCode;
}
