package org.iatoki.judgels.sandalphon.controllers.api.object.v2;

import org.iatoki.judgels.sandalphon.problem.programming.grading.LanguageRestriction;

import java.util.Map;

public class ProblemSubmissionConfigV2 {
    public Map<String, String> sourceKeys;
    public String gradingEngine;
    public LanguageRestriction gradingLanguageRestriction;
}
