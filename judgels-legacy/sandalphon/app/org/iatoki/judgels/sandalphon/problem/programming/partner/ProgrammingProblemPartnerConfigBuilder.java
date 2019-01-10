package org.iatoki.judgels.sandalphon.problem.programming.partner;

public class ProgrammingProblemPartnerConfigBuilder {

    private boolean isAllowedToSubmit;
    private boolean isAllowedToManageGrading;

    public ProgrammingProblemPartnerConfigBuilder setIsAllowedToSubmit(boolean isAllowedToSubmit) {
        this.isAllowedToSubmit = isAllowedToSubmit;
        return this;
    }

    public ProgrammingProblemPartnerConfigBuilder setIsAllowedToManageGrading(boolean isAllowedToManageGrading) {
        this.isAllowedToManageGrading = isAllowedToManageGrading;
        return this;
    }

    public ProgrammingProblemPartnerConfig build() {
        return new ProgrammingProblemPartnerConfig(isAllowedToSubmit, isAllowedToManageGrading);
    }
}
