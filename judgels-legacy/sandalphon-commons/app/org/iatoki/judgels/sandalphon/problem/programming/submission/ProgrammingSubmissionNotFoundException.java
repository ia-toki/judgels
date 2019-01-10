package org.iatoki.judgels.sandalphon.problem.programming.submission;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ProgrammingSubmissionNotFoundException extends EntityNotFoundException {

    public ProgrammingSubmissionNotFoundException() {
        super();
    }

    public ProgrammingSubmissionNotFoundException(String s) {
        super(s);
    }

    public ProgrammingSubmissionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProgrammingSubmissionNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Submission";
    }
}
