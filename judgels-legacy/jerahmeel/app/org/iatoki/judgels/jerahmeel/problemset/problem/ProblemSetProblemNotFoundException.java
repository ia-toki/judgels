package org.iatoki.judgels.jerahmeel.problemset.problem;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ProblemSetProblemNotFoundException extends EntityNotFoundException {

    public ProblemSetProblemNotFoundException() {
        super();
    }

    public ProblemSetProblemNotFoundException(String s) {
        super(s);
    }

    public ProblemSetProblemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProblemSetProblemNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Problem Set Problem";
    }
}
