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
}
