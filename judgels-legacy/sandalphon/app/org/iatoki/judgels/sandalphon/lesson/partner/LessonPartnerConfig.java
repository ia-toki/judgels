package org.iatoki.judgels.sandalphon.lesson.partner;

import java.util.Set;

public final class LessonPartnerConfig {

    private final boolean isAllowedToUpdateLesson;

    private final boolean isAllowedToUpdateStatement;
    private final boolean isAllowedToUploadStatementResources;
    private final Set<String> allowedStatementLanguagesToView;
    private final Set<String> allowedStatementLanguagesToUpdate;
    private final boolean isAllowedToManageStatementLanguages;

    private final boolean isAllowedToViewVersionHistory;
    private final boolean isAllowedToRestoreVersionHistory;

    private final boolean isAllowedToManageLessonClients;

    public LessonPartnerConfig(boolean isAllowedToUpdateLesson, boolean isAllowedToUpdateStatement, boolean isAllowedToUploadStatementResources, Set<String> allowedStatementLanguagesToView, Set<String> allowedStatementLanguagesToUpdate, boolean isAllowedToManageStatementLanguages, boolean isAllowedToViewVersionHistory, boolean isAllowedToRestoreVersionHistory, boolean isAllowedToManageLessonClients) {
        this.isAllowedToUpdateLesson = isAllowedToUpdateLesson;
        this.isAllowedToUpdateStatement = isAllowedToUpdateStatement;
        this.isAllowedToUploadStatementResources = isAllowedToUploadStatementResources;
        this.allowedStatementLanguagesToView = allowedStatementLanguagesToView;
        this.allowedStatementLanguagesToUpdate = allowedStatementLanguagesToUpdate;
        this.isAllowedToManageStatementLanguages = isAllowedToManageStatementLanguages;
        this.isAllowedToViewVersionHistory = isAllowedToViewVersionHistory;
        this.isAllowedToRestoreVersionHistory = isAllowedToRestoreVersionHistory;
        this.isAllowedToManageLessonClients = isAllowedToManageLessonClients;
    }

    public boolean isAllowedToUpdateLesson() {
        return isAllowedToUpdateLesson;
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

    public boolean isAllowedToManageLessonClients() {
        return isAllowedToManageLessonClients;
    }
}
