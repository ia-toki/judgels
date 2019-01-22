package org.iatoki.judgels.sandalphon.lesson.partner;

public final class LessonPartnerUpsertForm {

    public boolean isAllowedToUpdateLesson;
    public boolean isAllowedToUpdateStatement;
    public boolean isAllowedToUploadStatementResources;

    public String allowedStatementLanguagesToView;
    public String allowedStatementLanguagesToUpdate;
    public boolean isAllowedToManageStatementLanguages;

    public boolean isAllowedToViewVersionHistory;
    public boolean isAllowedToRestoreVersionHistory;

    public boolean isAllowedToManageLessonClients;

    public boolean isAllowedToUpdateLesson() {
        return isAllowedToUpdateLesson;
    }

    public void setIsAllowedToUpdateLesson(boolean allowedToUpdateLesson) {
        isAllowedToUpdateLesson = allowedToUpdateLesson;
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

    public boolean getIsAllowedToManageLessonClients() {
        return isAllowedToManageLessonClients;
    }

    public void setIsAllowedToManageLessonClients(boolean allowedToManageLessonClients) {
        isAllowedToManageLessonClients = allowedToManageLessonClients;
    }
}
