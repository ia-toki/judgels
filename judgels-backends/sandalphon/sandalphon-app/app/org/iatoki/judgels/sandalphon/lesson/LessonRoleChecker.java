package org.iatoki.judgels.sandalphon.lesson;

import static org.iatoki.judgels.jophiel.JophielSessionUtils.getUserJid;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.partner.LessonPartnerConfig;
import judgels.sandalphon.lesson.LessonStore;
import judgels.sandalphon.resource.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.role.RoleChecker;
import play.mvc.Http;

public class LessonRoleChecker {
    private final RoleChecker roleChecker;
    private final LessonStore lessonStore;

    @Inject
    public LessonRoleChecker(RoleChecker roleChecker, LessonStore lessonStore) {
        this.roleChecker = roleChecker;
        this.lessonStore = lessonStore;
    }

    public boolean isAuthor(Http.Request req, Lesson lesson) {
        return lesson.getAuthorJid().equals(getUserJid(req));
    }

    public boolean isAuthorOrAbove(Http.Request req, Lesson lesson) {
        return roleChecker.isAdmin(req) || isAuthor(req, lesson);
    }

    public boolean isPartner(Http.Request req, Lesson lesson) {
        return lessonStore.isUserPartnerForLesson(lesson.getJid(), getUserJid(req));
    }

    public boolean isPartnerOrAbove(Http.Request req, Lesson lesson) {
        return isAuthorOrAbove(req, lesson) || isPartner(req, lesson);
    }

    public boolean isAllowedToUpdateLesson(Http.Request req, Lesson lesson) {
        return isAuthorOrAbove(req, lesson)
                || (isPartner(req, lesson) && getPartnerConfig(req, lesson).getIsAllowedToUpdateLesson());
    }

    public boolean isAllowedToUploadStatementResources(Http.Request req, Lesson lesson) {
        return isAuthorOrAbove(req, lesson)
                || (isPartner(req, lesson) && getPartnerConfig(req, lesson).getIsAllowedToUploadStatementResources());
    }

    public boolean isAllowedToViewStatement(Http.Request req, Lesson lesson, String language) {
        if (isAuthorOrAbove(req, lesson)) {
            return true;
        }
        if (!isPartner(req, lesson)) {
            return false;
        }

        String defaultLanguage = lessonStore.getDefaultLanguage(getUserJid(req), lesson.getJid());
        Set<String> allowedLanguages = getPartnerConfig(req, lesson).getAllowedStatementLanguagesToView();

        return allowedLanguages.isEmpty() || allowedLanguages.contains(language) || language.equals(defaultLanguage);
    }

    public boolean isAllowedToUpdateStatement(Http.Request req, Lesson lesson) {
        return isAuthorOrAbove(req, lesson)
                || (isPartner(req, lesson) && getPartnerConfig(req, lesson).getIsAllowedToUpdateStatement());
    }

    public boolean isAllowedToUpdateStatementInLanguage(Http.Request req, Lesson lesson, String language) {
        if (!isAllowedToUpdateStatement(req, lesson)) {
            return false;
        }
        if (isAuthorOrAbove(req, lesson)) {
            return true;
        }
        if (!isPartner(req, lesson)) {
            return false;
        }

        Set<String> allowedLanguages = getPartnerConfig(req, lesson).getAllowedStatementLanguagesToUpdate();

        return allowedLanguages.isEmpty() || allowedLanguages.contains(language);
    }

    public boolean isAllowedToManageStatementLanguages(Http.Request req, Lesson lesson) {
        return isAuthorOrAbove(req, lesson)
                || (isPartner(req, lesson) && getPartnerConfig(req, lesson).getIsAllowedToManageStatementLanguages());
    }

    public boolean isAllowedToViewVersionHistory(Http.Request req, Lesson lesson) {
        return isAuthorOrAbove(req, lesson)
                || (isPartner(req, lesson) && getPartnerConfig(req, lesson).getIsAllowedToViewVersionHistory());
    }

    public boolean isAllowedToRestoreVersionHistory(Http.Request req, Lesson lesson) {
        return isAuthorOrAbove(req, lesson)
                || (isPartner(req, lesson) && getPartnerConfig(req, lesson).getIsAllowedToRestoreVersionHistory());
    }

    public Set<String> getAllowedLanguagesToView(Http.Request req, Lesson lesson) {
        Map<String, StatementLanguageStatus> availableLanguages =
                lessonStore.getAvailableLanguages(getUserJid(req), lesson.getJid());

        Set<String> allowedLanguages = Sets.newTreeSet();
        allowedLanguages.addAll(availableLanguages.entrySet().stream()
                .filter(e -> e.getValue() == StatementLanguageStatus.ENABLED)
                .map(e -> e.getKey())
                .collect(Collectors.toSet()));

        if (isPartner(req, lesson)) {
            Set<String> allowedPartnerLanguages = getPartnerConfig(req, lesson).getAllowedStatementLanguagesToView();
            if (!allowedPartnerLanguages.isEmpty()) {
                allowedLanguages.retainAll(allowedPartnerLanguages);
            }
            allowedLanguages.add(lessonStore.getDefaultLanguage(getUserJid(req), lesson.getJid()));
        }

        return ImmutableSet.copyOf(allowedLanguages);
    }

    public Set<String> getAllowedLanguagesToUpdate(Http.Request req, Lesson lesson) {
        Map<String, StatementLanguageStatus> availableLanguages =
                lessonStore.getAvailableLanguages(getUserJid(req), lesson.getJid());

        Set<String> allowedLanguages = Sets.newTreeSet();
        allowedLanguages.addAll(availableLanguages.entrySet().stream()
                .filter(e -> e.getValue() == StatementLanguageStatus.ENABLED)
                .map(e -> e.getKey())
                .collect(Collectors.toSet()));

        if (isPartner(req, lesson)) {
            Set<String> allowedPartnerLanguages = getPartnerConfig(req, lesson).getAllowedStatementLanguagesToUpdate();
            if (!allowedPartnerLanguages.isEmpty()) {
                allowedLanguages.retainAll(allowedPartnerLanguages);
            }
            allowedLanguages.add(lessonStore.getDefaultLanguage(getUserJid(req), lesson.getJid()));
        }

        return ImmutableSet.copyOf(allowedLanguages);
    }

    private LessonPartnerConfig getPartnerConfig(Http.Request req, Lesson lesson) {
        return lessonStore.findLessonPartnerByLessonJidAndPartnerJid(lesson.getJid(), getUserJid(req)).getConfig();
    }
}
