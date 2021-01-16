package org.iatoki.judgels.sandalphon.problem.base.partner;

import java.util.Set;

public final class ProblemPartnerConfig {

    private final boolean isAllowedToUpdateProblem;

    private final boolean isAllowedToUpdateStatement;
    private final boolean isAllowedToUploadStatementResources;
    private final Set<String> allowedStatementLanguagesToView;
    private final Set<String> allowedStatementLanguagesToUpdate;
    private final boolean isAllowedToManageStatementLanguages;

    private final boolean isAllowedToViewVersionHistory;
    private final boolean isAllowedToRestoreVersionHistory;

    private final boolean isAllowedToManageProblemClients;

    public ProblemPartnerConfig(boolean isAllowedToUpdateProblem, boolean isAllowedToUpdateStatement, boolean isAllowedToUploadStatementResources, Set<String> allowedStatementLanguagesToView, Set<String> allowedStatementLanguagesToUpdate, boolean isAllowedToManageStatementLanguages, boolean isAllowedToViewVersionHistory, boolean isAllowedToRestoreVersionHistory, boolean isAllowedToManageProblemClients) {
        this.isAllowedToUpdateProblem = isAllowedToUpdateProblem;
        this.isAllowedToUpdateStatement = isAllowedToUpdateStatement;
        this.isAllowedToUploadStatementResources = isAllowedToUploadStatementResources;
        this.allowedStatementLanguagesToView = allowedStatementLanguagesToView;
        this.allowedStatementLanguagesToUpdate = allowedStatementLanguagesToUpdate;
        this.isAllowedToManageStatementLanguages = isAllowedToManageStatementLanguages;
        this.isAllowedToViewVersionHistory = isAllowedToViewVersionHistory;
        this.isAllowedToRestoreVersionHistory = isAllowedToRestoreVersionHistory;
        this.isAllowedToManageProblemClients = isAllowedToManageProblemClients;
    }

    public boolean isAllowedToUpdateProblem() {
        return isAllowedToUpdateProblem;
    }

    public boolean isAllowedToUpdateStatement() {
        return isAllowedToUpdateStatement;
    }

    public boolean isAllowedToUploadStatementResources() {
        return isAllowedToUploadStatementResources;
    }

    public Set<String> getAllowedStatementLanguagesToView() {
        return allowedStatementLanguagesToView;
    }

    public Set<String> getAllowedStatementLanguagesToUpdate() {
        return allowedStatementLanguagesToUpdate;
    }

    public boolean isAllowedToManageStatementLanguages() {
        return isAllowedToManageStatementLanguages;
    }

    public boolean isAllowedToViewVersionHistory() {
        return isAllowedToViewVersionHistory;
    }

    public boolean isAllowedToRestoreVersionHistory() {
        return isAllowedToRestoreVersionHistory;
    }

    public boolean isAllowedToManageProblemClients() {
        return isAllowedToManageProblemClients;
    }
}
