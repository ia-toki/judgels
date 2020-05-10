package org.iatoki.judgels.sandalphon.problem.base.partner;

public final class ProblemPartnerUpsertForm {

    public boolean isAllowedToUpdateProblem;
    public boolean isAllowedToUpdateStatement;
    public boolean isAllowedToUploadStatementResources;

    public String allowedStatementLanguagesToView;
    public String allowedStatementLanguagesToUpdate;
    public boolean isAllowedToManageStatementLanguages;

    public boolean isAllowedToViewVersionHistory;
    public boolean isAllowedToRestoreVersionHistory;

    public boolean isAllowedToManageProblemClients;

    public boolean getIsAllowedToUpdateProblem() {
        return isAllowedToUpdateProblem;
    }

    public void setIsAllowedToUpdateProblem(boolean allowedToUpdateProblem) {
        isAllowedToUpdateProblem = allowedToUpdateProblem;
    }

    public boolean getIsAllowedToUpdateStatement() {
        return isAllowedToUpdateStatement;
    }

    public void setIsAllowedToUpdateStatement(boolean allowedToUpdateStatement) {
        isAllowedToUpdateStatement = allowedToUpdateStatement;
    }

    public boolean getIsAllowedToUploadStatementResources() {
        return isAllowedToUploadStatementResources;
    }

    public void setIsAllowedToUploadStatementResources(boolean allowedToUploadStatementResources) {
        isAllowedToUploadStatementResources = allowedToUploadStatementResources;
    }

    public String getAllowedStatementLanguagesToView() {
        return allowedStatementLanguagesToView;
    }

    public void setAllowedStatementLanguagesToView(String allowedStatementLanguagesToView) {
        this.allowedStatementLanguagesToView = allowedStatementLanguagesToView;
    }

    public String getAllowedStatementLanguagesToUpdate() {
        return allowedStatementLanguagesToUpdate;
    }

    public void setAllowedStatementLanguagesToUpdate(String allowedStatementLanguagesToUpdate) {
        this.allowedStatementLanguagesToUpdate = allowedStatementLanguagesToUpdate;
    }

    public boolean getIsAllowedToManageStatementLanguages() {
        return isAllowedToManageStatementLanguages;
    }

    public void setIsAllowedToManageStatementLanguages(boolean allowedToManageStatementLanguages) {
        isAllowedToManageStatementLanguages = allowedToManageStatementLanguages;
    }

    public boolean getIsAllowedToViewVersionHistory() {
        return isAllowedToViewVersionHistory;
    }

    public void setIsAllowedToViewVersionHistory(boolean allowedToViewVersionHistory) {
        isAllowedToViewVersionHistory = allowedToViewVersionHistory;
    }

    public boolean getIsAllowedToRestoreVersionHistory() {
        return isAllowedToRestoreVersionHistory;
    }

    public void setIsAllowedToRestoreVersionHistory(boolean allowedToRestoreVersionHistory) {
        isAllowedToRestoreVersionHistory = allowedToRestoreVersionHistory;
    }

    public boolean getIsAllowedToManageProblemClients() {
        return isAllowedToManageProblemClients;
    }

    public void setIsAllowedToManageProblemClients(boolean allowedToManageProblemClients) {
        isAllowedToManageProblemClients = allowedToManageProblemClients;
    }
}
