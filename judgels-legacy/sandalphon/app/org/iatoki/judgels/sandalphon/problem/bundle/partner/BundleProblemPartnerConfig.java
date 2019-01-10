package org.iatoki.judgels.sandalphon.problem.bundle.partner;

import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerChildConfig;

public final class BundleProblemPartnerConfig implements ProblemPartnerChildConfig {

    private final boolean isAllowedToSubmit;
    private final boolean isAllowedToManageItems;

    public BundleProblemPartnerConfig(boolean isAllowedToSubmit, boolean isAllowedToManageItems) {
        this.isAllowedToSubmit = isAllowedToSubmit;
        this.isAllowedToManageItems = isAllowedToManageItems;
    }

    public boolean isAllowedToSubmit() {
        return isAllowedToSubmit;
    }

    public boolean isAllowedToManageItems() {
        return isAllowedToManageItems;
    }
}
