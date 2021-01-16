package org.iatoki.judgels.sandalphon.problem.base.partner;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ProblemPartnerNotFoundException extends EntityNotFoundException {

    public ProblemPartnerNotFoundException() {
        super();
    }

    public ProblemPartnerNotFoundException(String s) {
        super(s);
    }

    public ProblemPartnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProblemPartnerNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Problem Partner";
    }
}
