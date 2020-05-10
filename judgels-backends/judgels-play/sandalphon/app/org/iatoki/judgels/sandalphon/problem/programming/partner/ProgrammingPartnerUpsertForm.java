package org.iatoki.judgels.sandalphon.problem.programming.partner;

public final class ProgrammingPartnerUpsertForm {

    public boolean isAllowedToSubmit;

    public boolean isAllowedToManageGrading;

    public boolean getIsAllowedToSubmit() {
        return isAllowedToSubmit;
    }

    public void setIsAllowedToSubmit(boolean allowedToSubmit) {
        isAllowedToSubmit = allowedToSubmit;
    }

    public boolean getIsAllowedToManageGrading() {
        return isAllowedToManageGrading;
    }

    public void setIsAllowedToManageGrading(boolean allowedToManageGrading) {
        isAllowedToManageGrading = allowedToManageGrading;
    }
}
