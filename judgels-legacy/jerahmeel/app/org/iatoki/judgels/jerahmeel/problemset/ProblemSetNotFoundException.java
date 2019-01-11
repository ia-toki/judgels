package org.iatoki.judgels.jerahmeel.problemset;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ProblemSetNotFoundException extends EntityNotFoundException {

    public ProblemSetNotFoundException() {
        super();
    }

    public ProblemSetNotFoundException(String s) {
        super(s);
    }

    public ProblemSetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProblemSetNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Problem Set";
    }
}
