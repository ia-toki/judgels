package org.iatoki.judgels.sandalphon.problem.programming.grading;

import play.data.validation.Constraints;

public class GradingEngineEditForm {

    @Constraints.Required
    public String gradingEngineName;
}
