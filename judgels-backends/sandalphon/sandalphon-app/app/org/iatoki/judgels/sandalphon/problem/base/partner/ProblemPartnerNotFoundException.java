package org.iatoki.judgels.sandalphon.problem.base.partner;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ProblemPartnerNotFoundException extends EntityNotFoundException {

    public ProblemPartnerNotFoundException(String s) {
        super(s);
    }

    @Override
    public String getEntityName() {
        return "Problem Partner";
    }
}
