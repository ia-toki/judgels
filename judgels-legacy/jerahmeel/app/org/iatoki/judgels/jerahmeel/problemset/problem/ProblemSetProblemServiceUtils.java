package org.iatoki.judgels.jerahmeel.problemset.problem;

final class ProblemSetProblemServiceUtils {

    private ProblemSetProblemServiceUtils() {
        // prevent instantiation
    }

    static ProblemSetProblem createFromModel(ProblemSetProblemModel model) {
        return new ProblemSetProblem(model.id, model.problemSetJid, model.problemJid, model.problemSecret, model.alias, ProblemSetProblemType.valueOf(model.type), ProblemSetProblemStatus.valueOf(model.status));
    }
}
