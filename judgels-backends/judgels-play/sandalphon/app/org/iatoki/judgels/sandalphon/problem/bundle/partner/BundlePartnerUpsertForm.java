package org.iatoki.judgels.sandalphon.problem.bundle.partner;

public final class BundlePartnerUpsertForm {

    public boolean isAllowedToSubmit;

    public boolean isAllowedToManageItems;

    public boolean getIsAllowedToSubmit() {
        return isAllowedToSubmit;
    }

    public void setIsAllowedToSubmit(boolean allowedToSubmit) {
        isAllowedToSubmit = allowedToSubmit;
    }

    public boolean getIsAllowedToManageItems() {
        return isAllowedToManageItems;
    }

    public void setIsAllowedToManageItems(boolean allowedToManageItems) {
        isAllowedToManageItems = allowedToManageItems;
    }
}
