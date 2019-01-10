package org.iatoki.judgels.sandalphon.problem.programming.partner;

import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerChildConfig;

public final class ProgrammingProblemPartnerConfig implements ProblemPartnerChildConfig {

    private final boolean isAllowedToSubmit;
    private final boolean isAllowedToManageGrading;

    public ProgrammingProblemPartnerConfig(boolean isAllowedToSubmit, boolean isAllowedToManageGrading) {
        this.isAllowedToSubmit = isAllowedToSubmit;
        this.isAllowedToManageGrading = isAllowedToManageGrading;
    }

    public boolean isAllowedToSubmit() {
        return isAllowedToSubmit;
    }

    public boolean isAllowedToManageGrading() {
        return isAllowedToManageGrading;
    }
}
