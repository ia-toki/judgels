package org.iatoki.judgels.sandalphon.lesson;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.partner.LessonPartnerConfig;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import play.mvc.Result;
import play.mvc.Results;

public final class LessonControllerUtils {

    private LessonControllerUtils() {
        // prevent instantiation
    }

    public static Result downloadFile(File file) {
        if (!file.exists()) {
            return Results.notFound();
        }
        return Results.ok(file)
                .as("application/x-download")
                .withHeader("Content-disposition", "attachment; filename=" + file.getName());
    }

    public static boolean isAuthor(Lesson lesson) {
        return lesson.getAuthorJid().equals(IdentityUtils.getUserJid());
    }

    public static boolean isAuthorOrAbove(Lesson lesson) {
        return SandalphonControllerUtils.getInstance().isAdmin() || isAuthor(lesson);
    }

    public static boolean isPartner(LessonService lessonService, Lesson lesson) {
        return lessonService.isUserPartnerForLesson(lesson.getJid(), IdentityUtils.getUserJid());
    }

    public static boolean isPartnerOrAbove(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || isPartner(lessonService, lesson);
    }

    public static boolean isAllowedToUpdateLesson(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).getIsAllowedToUpdateLesson());
    }

    public static boolean isAllowedToUploadStatementResources(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).getIsAllowedToUploadStatementResources());
    }

    public static boolean isAllowedToViewStatement(LessonService lessonService, Lesson lesson, String language) {
        if (isAuthorOrAbove(lesson)) {
            return true;
        }

        if (!isPartner(lessonService, lesson)) {
            return false;
        }

        String defaultLanguage = lessonService.getDefaultLanguage(IdentityUtils.getUserJid(), lesson.getJid());
        Set<String> allowedLanguages = getPartnerConfig(lessonService, lesson).getAllowedStatementLanguagesToView();

        return allowedLanguages == null || allowedLanguages.contains(language) || language.equals(defaultLanguage);
    }


    public static boolean isAllowedToUpdateStatement(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).getIsAllowedToUpdateStatement());
    }

    public static boolean isAllowedToUpdateStatementInLanguage(LessonService lessonService, Lesson lesson, String language) {
        if (!isAllowedToUpdateStatement(lessonService, lesson)) {
            return false;
        }

        if (isAuthorOrAbove(lesson)) {
            return true;
        }

        if (!isPartner(lessonService, lesson)) {
            return false;
        }

        Set<String> allowedLanguages = getPartnerConfig(lessonService, lesson).getAllowedStatementLanguagesToUpdate();

        return allowedLanguages == null || allowedLanguages.contains(language);
    }

    public static boolean isAllowedToManageStatementLanguages(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).getIsAllowedToManageStatementLanguages());
    }

    public static boolean isAllowedToViewVersionHistory(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).getIsAllowedToViewVersionHistory());
    }

    public static boolean isAllowedToRestoreVersionHistory(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).getIsAllowedToRestoreVersionHistory());
    }

    public static LessonPartnerConfig getPartnerConfig(LessonService lessonService, Lesson lesson) {
        return lessonService.findLessonPartnerByLessonJidAndPartnerJid(lesson.getJid(), IdentityUtils.getUserJid()).getConfig();
    }

    public static Set<String> getAllowedLanguagesToView(LessonService lessonService, Lesson lesson) throws IOException {
        Map<String, StatementLanguageStatus> availableLanguages = lessonService.getAvailableLanguages(IdentityUtils.getUserJid(), lesson.getJid());

        Set<String> allowedLanguages = Sets.newTreeSet();
        allowedLanguages.addAll(availableLanguages.entrySet().stream().filter(e -> e.getValue() == StatementLanguageStatus.ENABLED).map(e -> e.getKey()).collect(Collectors.toSet()));

        if (isPartner(lessonService, lesson)) {
            Set<String> allowedPartnerLanguages = getPartnerConfig(lessonService, lesson).getAllowedStatementLanguagesToView();
            if (allowedPartnerLanguages != null) {
                allowedLanguages.retainAll(allowedPartnerLanguages);
                allowedLanguages.add(lessonService.getDefaultLanguage(IdentityUtils.getUserJid(), lesson.getJid()));
            }
        }

        return ImmutableSet.copyOf(allowedLanguages);
    }

    public static Set<String> getAllowedLanguagesToUpdate(LessonService lessonService, Lesson lesson) throws IOException {
        Map<String, StatementLanguageStatus> availableLanguages = lessonService.getAvailableLanguages(IdentityUtils.getUserJid(), lesson.getJid());

        Set<String> allowedLanguages = Sets.newTreeSet();
        allowedLanguages.addAll(availableLanguages.entrySet().stream().filter(e -> e.getValue() == StatementLanguageStatus.ENABLED).map(e -> e.getKey()).collect(Collectors.toSet()));

        if (isPartner(lessonService, lesson) && isAllowedToUpdateStatement(lessonService, lesson)) {
            Set<String> allowedPartnerLanguages = getPartnerConfig(lessonService, lesson).getAllowedStatementLanguagesToUpdate();
            if (allowedPartnerLanguages != null) {
                allowedLanguages.retainAll(allowedPartnerLanguages);
            }
        }

        return ImmutableSet.copyOf(allowedLanguages);
    }
}
