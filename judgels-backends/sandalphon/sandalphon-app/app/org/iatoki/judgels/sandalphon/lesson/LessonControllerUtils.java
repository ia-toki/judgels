package org.iatoki.judgels.sandalphon.lesson;

import judgels.sandalphon.api.lesson.Lesson;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.lesson.partner.LessonPartnerConfig;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class LessonControllerUtils {

    private LessonControllerUtils() {
        // prevent instantiation
    }

    public static void establishStatementLanguage(LessonService lessonService, Lesson lesson) throws IOException {
        String currentLanguage = getCurrentStatementLanguage();
        Map<String, StatementLanguageStatus> availableLanguages = lessonService.getAvailableLanguages(IdentityUtils.getUserJid(), lesson.getJid());

        if (currentLanguage == null || !availableLanguages.containsKey(currentLanguage) || availableLanguages.get(currentLanguage) == StatementLanguageStatus.DISABLED) {
            String languageCode = lessonService.getDefaultLanguage(IdentityUtils.getUserJid(), lesson.getJid());
            setCurrentStatementLanguage(languageCode);
        }
    }

    public static String getDefaultStatementLanguage(LessonService lessonService, Lesson lesson) throws IOException {
        return lessonService.getDefaultLanguage(IdentityUtils.getUserJid(), lesson.getJid());
    }

    public static void setCurrentStatementLanguage(String languageCode) {
        Controller.session("currentStatementLanguage", languageCode);
    }

    public static String getCurrentStatementLanguage() {
        return Controller.session("currentStatementLanguage");
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
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).isAllowedToUpdateLesson());
    }

    public static boolean isAllowedToUploadStatementResources(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).isAllowedToUploadStatementResources());
    }

    public static boolean isAllowedToViewStatement(LessonService lessonService, Lesson lesson) {
        if (isAuthorOrAbove(lesson)) {
            return true;
        }

        if (!isPartner(lessonService, lesson)) {
            return false;
        }

        String language = getCurrentStatementLanguage();

        try {
            String defaultLanguage = lessonService.getDefaultLanguage(IdentityUtils.getUserJid(), lesson.getJid());
            Set<String> allowedLanguages = getPartnerConfig(lessonService, lesson).getAllowedStatementLanguagesToView();

            if (allowedLanguages == null || allowedLanguages.contains(language) || language.equals(defaultLanguage)) {
                return true;
            }

            setCurrentStatementLanguage(defaultLanguage);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public static boolean isAllowedToUpdateStatement(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).isAllowedToUpdateStatement());
    }

    public static boolean isAllowedToUpdateStatementInLanguage(LessonService lessonService, Lesson lesson) {
        if (!isAllowedToUpdateStatement(lessonService, lesson)) {
            return false;
        }

        if (isAuthorOrAbove(lesson)) {
            return true;
        }

        if (!isPartner(lessonService, lesson)) {
            return false;
        }

        String language = getCurrentStatementLanguage();

        Set<String> allowedLanguages = getPartnerConfig(lessonService, lesson).getAllowedStatementLanguagesToUpdate();

        if (allowedLanguages == null || allowedLanguages.contains(language)) {
            return true;
        }

        String firstLanguage = allowedLanguages.iterator().next();

        setCurrentStatementLanguage(firstLanguage);
        return true;
    }

    public static boolean isAllowedToManageStatementLanguages(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).isAllowedToManageStatementLanguages());
    }

    public static boolean isAllowedToViewVersionHistory(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).isAllowedToViewVersionHistory());
    }

    public static boolean isAllowedToRestoreVersionHistory(LessonService lessonService, Lesson lesson) {
        return isAuthorOrAbove(lesson) || (isPartner(lessonService, lesson) && getPartnerConfig(lessonService, lesson).isAllowedToRestoreVersionHistory());
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
