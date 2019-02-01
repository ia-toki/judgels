package org.iatoki.judgels.sandalphon;

import com.google.gson.Gson;
import judgels.sandalphon.api.lesson.LessonInfo;
import judgels.sandalphon.api.problem.ProblemInfo;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayName;

public class SandalphonResourceDisplayNames {
    private SandalphonResourceDisplayNames() {}

    public static String getProblemDisplayName(ProblemInfo problem) {
        return new Gson().toJson(new SandalphonResourceDisplayName(problem.getSlug(), problem.getDefaultLanguage(), problem.getTitlesByLanguage()));
    }

    public static String getLessonDisplayName(LessonInfo lesson) {
        return new Gson().toJson(new SandalphonResourceDisplayName(lesson.getSlug(), lesson.getDefaultLanguage(), lesson.getTitlesByLanguage()));
    }
}
