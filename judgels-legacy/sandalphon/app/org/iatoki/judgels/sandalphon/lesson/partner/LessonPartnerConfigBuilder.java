package org.iatoki.judgels.sandalphon.lesson.partner;

import java.util.Set;

public class LessonPartnerConfigBuilder {

    private boolean isAllowedToUpdateLesson;
    private boolean isAllowedToUpdateStatement;
    private boolean isAllowedToUploadStatementResources;
    private Set<String> allowedStatementLanguagesToView;
    private Set<String> allowedStatementLanguagesToUpdate;
    private boolean isAllowedToManageStatementLanguages;
    private boolean isAllowedToViewVersionHistory;
    private boolean isAllowedToRestoreVersionHistory;
    private boolean isAllowedToManageLessonClients;

    public LessonPartnerConfigBuilder setIsAllowedToUpdateLesson(boolean isAllowedToUpdateLesson) {
        this.isAllowedToUpdateLesson = isAllowedToUpdateLesson;
        return this;
    }

    public LessonPartnerConfigBuilder setIsAllowedToUpdateStatement(boolean isAllowedToUpdateStatement) {
        this.isAllowedToUpdateStatement = isAllowedToUpdateStatement;
        return this;
    }

    public LessonPartnerConfigBuilder setIsAllowedToUploadStatementResources(boolean isAllowedToUploadStatementResources) {
        this.isAllowedToUploadStatementResources = isAllowedToUploadStatementResources;
        return this;
    }

    public LessonPartnerConfigBuilder setAllowedStatementLanguagesToView(Set<String> allowedStatementLanguagesToView) {
        this.allowedStatementLanguagesToView = allowedStatementLanguagesToView;
        return this;
    }

    public LessonPartnerConfigBuilder setAllowedStatementLanguagesToUpdate(Set<String> allowedStatementLanguagesToUpdate) {
        this.allowedStatementLanguagesToUpdate = allowedStatementLanguagesToUpdate;
        return this;
    }

    public LessonPartnerConfigBuilder setIsAllowedToManageStatementLanguages(boolean isAllowedToManageStatementLanguages) {
        this.isAllowedToManageStatementLanguages = isAllowedToManageStatementLanguages;
        return this;
    }

    public LessonPartnerConfigBuilder setIsAllowedToViewVersionHistory(boolean isAllowedToViewVersionHistory) {
        this.isAllowedToViewVersionHistory = isAllowedToViewVersionHistory;
        return this;
    }

    public LessonPartnerConfigBuilder setIsAllowedToRestoreVersionHistory(boolean isAllowedToRestoreVersionHistory) {
        this.isAllowedToRestoreVersionHistory = isAllowedToRestoreVersionHistory;
        return this;
    }

    public LessonPartnerConfigBuilder setIsAllowedToManageLessonClients(boolean isAllowedToManageLessonClients) {
        this.isAllowedToManageLessonClients = isAllowedToManageLessonClients;
        return this;
    }

    public LessonPartnerConfig build() {
        return new LessonPartnerConfig(isAllowedToUpdateLesson, isAllowedToUpdateStatement, isAllowedToUploadStatementResources, allowedStatementLanguagesToView, allowedStatementLanguagesToUpdate, isAllowedToManageStatementLanguages, isAllowedToViewVersionHistory, isAllowedToRestoreVersionHistory, isAllowedToManageLessonClients);
    }
}
