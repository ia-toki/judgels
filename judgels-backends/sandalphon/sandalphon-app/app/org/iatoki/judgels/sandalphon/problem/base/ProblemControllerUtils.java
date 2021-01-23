package org.iatoki.judgels.sandalphon.problem.base;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.partner.ProblemPartnerConfig;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import play.mvc.Controller;

public final class ProblemControllerUtils {

    private ProblemControllerUtils() {
        // prevent instantiation
    }

    public static void setJustCreatedProblem(String slug, String additionalNote, String initLanguageCode) {
        Controller.session("problemSlug", slug);
        Controller.session("problemAdditionalNote", additionalNote);
        Controller.session("initLanguageCode", initLanguageCode);
    }

    public static void removeJustCreatedProblem() {
        Controller.session().remove("problemSlug");
        Controller.session().remove("problemAdditionalNote");
        Controller.session().remove("initLanguageCode");
    }

    public static String getJustCreatedProblemSlug() {
        return Controller.session("problemSlug");
    }

    public static String getJustCreatedProblemAdditionalNote() {
        return Controller.session("problemAdditionalNote");
    }

    public static String getJustCreatedProblemInitLanguageCode() {
        return Controller.session("initLanguageCode");
    }

    public static boolean wasProblemJustCreated() {
        return getJustCreatedProblemSlug() != null
                && getJustCreatedProblemAdditionalNote() != null
                && getJustCreatedProblemInitLanguageCode() != null;
    }

    public static boolean isAuthor(Problem problem) {
        return problem.getAuthorJid().equals(IdentityUtils.getUserJid());
    }

    public static boolean isAuthorOrAbove(Problem problem) {
        return SandalphonControllerUtils.getInstance().isAdmin() || isAuthor(problem);
    }

    public static boolean isPartner(ProblemService problemService, Problem problem) {
        return problemService.isUserPartnerForProblem(problem.getJid(), IdentityUtils.getUserJid());
    }

    public static boolean isPartnerOrAbove(ProblemService problemService, Problem problem) {
        return isAuthorOrAbove(problem) || isPartner(problemService, problem);
    }

    public static boolean isAllowedToUpdateProblem(ProblemService problemService, Problem problem) {
        return isAuthorOrAbove(problem) || (isPartner(problemService, problem) && getPartnerConfig(problemService, problem).getIsAllowedToUpdateProblem());
    }

    public static boolean isAllowedToUploadStatementResources(ProblemService problemService, Problem problem) {
        return isAuthorOrAbove(problem) || (isPartner(problemService, problem) && getPartnerConfig(problemService, problem).getIsAllowedToUploadStatementResources());
    }

    public static boolean isAllowedToViewStatement(ProblemService problemService, Problem problem, String language) {
        if (isAuthorOrAbove(problem)) {
            return true;
        }

        if (!isPartner(problemService, problem)) {
            return false;
        }

        String defaultLanguage = problemService.getDefaultLanguage(IdentityUtils.getUserJid(), problem.getJid());
        Set<String> allowedLanguages = getPartnerConfig(problemService, problem).getAllowedStatementLanguagesToView();

        return allowedLanguages == null || allowedLanguages.contains(language) || language.equals(defaultLanguage);
    }


    public static boolean isAllowedToUpdateStatement(ProblemService problemService, Problem problem) {
        return isAuthorOrAbove(problem) || (isPartner(problemService, problem) && getPartnerConfig(problemService, problem).getIsAllowedToUpdateStatement());
    }

    public static boolean isAllowedToUpdateStatementInLanguage(ProblemService problemService, Problem problem, String language) {
        if (!isAllowedToUpdateStatement(problemService, problem)) {
            return false;
        }

        if (isAuthorOrAbove(problem)) {
            return true;
        }

        if (!isPartner(problemService, problem)) {
            return false;
        }

        Set<String> allowedLanguages = getPartnerConfig(problemService, problem).getAllowedStatementLanguagesToUpdate();

        return allowedLanguages == null || allowedLanguages.contains(language);
    }

    public static boolean isAllowedToManageStatementLanguages(ProblemService problemService, Problem problem) {
        return isAuthorOrAbove(problem) || (isPartner(problemService, problem) && getPartnerConfig(problemService, problem).getIsAllowedToManageStatementLanguages());
    }

    public static boolean isAllowedToViewVersionHistory(ProblemService problemService, Problem problem) {
        return isAuthorOrAbove(problem) || (isPartner(problemService, problem) && getPartnerConfig(problemService, problem).getIsAllowedToViewVersionHistory());
    }

    public static boolean isAllowedToRestoreVersionHistory(ProblemService problemService, Problem problem) {
        return isAuthorOrAbove(problem) || (isPartner(problemService, problem) && getPartnerConfig(problemService, problem).getIsAllowedToRestoreVersionHistory());
    }

    public static ProblemPartnerConfig getPartnerConfig(ProblemService problemService, Problem problem) {
        return problemService.findProblemPartnerByProblemJidAndPartnerJid(problem.getJid(), IdentityUtils.getUserJid()).getBaseConfig();
    }

    public static Set<String> getAllowedLanguagesToView(ProblemService problemService, Problem problem) throws IOException {
        Map<String, StatementLanguageStatus> availableLanguages = problemService.getAvailableLanguages(IdentityUtils.getUserJid(), problem.getJid());

        Set<String> allowedLanguages = Sets.newTreeSet();
        allowedLanguages.addAll(availableLanguages.entrySet().stream().filter(e -> e.getValue() == StatementLanguageStatus.ENABLED).map(e -> e.getKey()).collect(Collectors.toSet()));

        if (isPartner(problemService, problem)) {
            Set<String> allowedPartnerLanguages = getPartnerConfig(problemService, problem).getAllowedStatementLanguagesToView();
            if (allowedPartnerLanguages != null) {
                allowedLanguages.retainAll(allowedPartnerLanguages);
                allowedLanguages.add(problemService.getDefaultLanguage(IdentityUtils.getUserJid(), problem.getJid()));
            }
        }

        return ImmutableSet.copyOf(allowedLanguages);
    }

    public static Set<String> getAllowedLanguagesToUpdate(ProblemService problemService, Problem problem) throws IOException {
        Map<String, StatementLanguageStatus> availableLanguages = problemService.getAvailableLanguages(IdentityUtils.getUserJid(), problem.getJid());

        Set<String> allowedLanguages = Sets.newTreeSet();
        allowedLanguages.addAll(availableLanguages.entrySet().stream().filter(e -> e.getValue() == StatementLanguageStatus.ENABLED).map(e -> e.getKey()).collect(Collectors.toSet()));

        if (isPartner(problemService, problem) && isAllowedToUpdateStatement(problemService, problem)) {
            Set<String> allowedPartnerLanguages = getPartnerConfig(problemService, problem).getAllowedStatementLanguagesToUpdate();
            if (allowedPartnerLanguages != null) {
                allowedLanguages.retainAll(allowedPartnerLanguages);
            }
        }

        return ImmutableSet.copyOf(allowedLanguages);
    }
}
