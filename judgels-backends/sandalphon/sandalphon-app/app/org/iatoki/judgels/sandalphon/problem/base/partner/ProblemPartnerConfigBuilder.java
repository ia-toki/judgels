package org.iatoki.judgels.sandalphon.problem.base.partner;

import java.util.Set;

public class ProblemPartnerConfigBuilder {

    private boolean isAllowedToUpdateProblem;
    private boolean isAllowedToUpdateStatement;
    private boolean isAllowedToUploadStatementResources;
    private Set<String> allowedStatementLanguagesToView;
    private Set<String> allowedStatementLanguagesToUpdate;
    private boolean isAllowedToManageStatementLanguages;
    private boolean isAllowedToViewVersionHistory;
    private boolean isAllowedToRestoreVersionHistory;
    private boolean isAllowedToManageProblemClients;

    public ProblemPartnerConfigBuilder setIsAllowedToUpdateProblem(boolean isAllowedToUpdateProblem) {
        this.isAllowedToUpdateProblem = isAllowedToUpdateProblem;
        return this;
    }

    public ProblemPartnerConfigBuilder setIsAllowedToUpdateStatement(boolean isAllowedToUpdateStatement) {
        this.isAllowedToUpdateStatement = isAllowedToUpdateStatement;
        return this;
    }

    public ProblemPartnerConfigBuilder setIsAllowedToUploadStatementResources(boolean isAllowedToUploadStatementResources) {
        this.isAllowedToUploadStatementResources = isAllowedToUploadStatementResources;
        return this;
    }

    public ProblemPartnerConfigBuilder setAllowedStatementLanguagesToView(Set<String> allowedStatementLanguagesToView) {
        this.allowedStatementLanguagesToView = allowedStatementLanguagesToView;
        return this;
    }

    public ProblemPartnerConfigBuilder setAllowedStatementLanguagesToUpdate(Set<String> allowedStatementLanguagesToUpdate) {
        this.allowedStatementLanguagesToUpdate = allowedStatementLanguagesToUpdate;
        return this;
    }

    public ProblemPartnerConfigBuilder setIsAllowedToManageStatementLanguages(boolean isAllowedToManageStatementLanguages) {
        this.isAllowedToManageStatementLanguages = isAllowedToManageStatementLanguages;
        return this;
    }

    public ProblemPartnerConfigBuilder setIsAllowedToViewVersionHistory(boolean isAllowedToViewVersionHistory) {
        this.isAllowedToViewVersionHistory = isAllowedToViewVersionHistory;
        return this;
    }

    public ProblemPartnerConfigBuilder setIsAllowedToRestoreVersionHistory(boolean isAllowedToRestoreVersionHistory) {
        this.isAllowedToRestoreVersionHistory = isAllowedToRestoreVersionHistory;
        return this;
    }

    public ProblemPartnerConfigBuilder setIsAllowedToManageProblemClients(boolean isAllowedToManageProblemClients) {
        this.isAllowedToManageProblemClients = isAllowedToManageProblemClients;
        return this;
    }

    public ProblemPartnerConfig build() {
        return new ProblemPartnerConfig(isAllowedToUpdateProblem, isAllowedToUpdateStatement, isAllowedToUploadStatementResources, allowedStatementLanguagesToView, allowedStatementLanguagesToUpdate, isAllowedToManageStatementLanguages, isAllowedToViewVersionHistory, isAllowedToRestoreVersionHistory, isAllowedToManageProblemClients);
    }
}
