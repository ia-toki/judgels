package org.iatoki.judgels.sandalphon.problem.base;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ProblemNotFoundException extends EntityNotFoundException {

    public ProblemNotFoundException(String s) {
        super(s);
    }

    @Override
    public String getEntityName() {
        return "Problem";
    }
}
