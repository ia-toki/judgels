package org.iatoki.judgels.sandalphon.problem.base;

import static org.iatoki.judgels.jophiel.JophielSessionUtils.getUserJid;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.partner.ProblemPartnerChildConfig;
import judgels.sandalphon.api.problem.partner.ProblemPartnerConfig;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.resource.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.role.RoleChecker;
import play.mvc.Http;

public class ProblemRoleChecker {
    private final RoleChecker roleChecker;
    private final ProblemStore problemStore;

    @Inject
    public ProblemRoleChecker(RoleChecker roleChecker, ProblemStore problemStore) {
        this.roleChecker = roleChecker;
        this.problemStore = problemStore;
    }

    public boolean isAuthor(Http.Request req, Problem problem) {
        return problem.getAuthorJid().equals(getUserJid(req));
    }

    public boolean isAuthorOrAbove(Http.Request req, Problem problem) {
        return roleChecker.isAdmin(req) || isAuthor(req, problem);
    }

    public boolean isPartner(Http.Request req, Problem problem) {
        return problemStore.isUserPartnerForProblem(problem.getJid(), getUserJid(req));
    }

    public boolean isPartnerOrAbove(Http.Request req, Problem problem) {
        return isAuthorOrAbove(req, problem) || isPartner(req, problem);
    }

    public boolean isAllowedToUpdateProblem(Http.Request req, Problem problem) {
        return isAuthorOrAbove(req, problem)
                || (isPartner(req, problem) && getPartnerConfig(req, problem).getIsAllowedToUpdateProblem());
    }

    public boolean isAllowedToUploadStatementResources(Http.Request req, Problem problem) {
        return isAuthorOrAbove(req, problem)
                || (isPartner(req, problem) && getPartnerConfig(req, problem).getIsAllowedToUploadStatementResources());
    }

    public boolean isAllowedToViewStatement(Http.Request req, Problem problem, String language) {
        if (isAuthorOrAbove(req, problem)) {
            return true;
        }
        if (!isPartner(req, problem)) {
            return false;
        }

        String defaultLanguage = problemStore.getStatementDefaultLanguage(getUserJid(req), problem.getJid());
        Set<String> allowedLanguages = getPartnerConfig(req, problem).getAllowedStatementLanguagesToView();

        return allowedLanguages.isEmpty() || allowedLanguages.contains(language) || language.equals(defaultLanguage);
    }

    public boolean isAllowedToUpdateStatement(Http.Request req, Problem problem) {
        return isAuthorOrAbove(req, problem)
                || (isPartner(req, problem) && getPartnerConfig(req, problem).getIsAllowedToUpdateStatement());
    }

    public boolean isAllowedToUpdateStatementInLanguage(Http.Request req, Problem problem, String language) {
        if (!isAllowedToUpdateStatement(req, problem)) {
            return false;
        }
        if (isAuthorOrAbove(req, problem)) {
            return true;
        }
        if (!isPartner(req, problem)) {
            return false;
        }

        Set<String> allowedLanguages = getPartnerConfig(req, problem).getAllowedStatementLanguagesToUpdate();

        return allowedLanguages.isEmpty() || allowedLanguages.contains(language);
    }

    public boolean isAllowedToManageStatementLanguages(Http.Request req, Problem problem) {
        return isAuthorOrAbove(req, problem)
                || (isPartner(req, problem) && getPartnerConfig(req, problem).getIsAllowedToManageStatementLanguages());
    }

    public boolean isAllowedToViewVersionHistory(Http.Request req, Problem problem) {
        return isAuthorOrAbove(req, problem)
                || (isPartner(req, problem) && getPartnerConfig(req, problem).getIsAllowedToViewVersionHistory());
    }

    public boolean isAllowedToRestoreVersionHistory(Http.Request req, Problem problem) {
        return isAuthorOrAbove(req, problem)
                || (isPartner(req, problem) && getPartnerConfig(req, problem).getIsAllowedToRestoreVersionHistory());
    }

    public boolean isAllowedToSubmit(Http.Request req, Problem problem) {
        return isAuthorOrAbove(req, problem)
                || (isPartner(req, problem) && getPartnerChildConfig(req, problem).getIsAllowedToSubmit());
    }

    public boolean isAllowedToManageGrading(Http.Request req, Problem problem) {
        return isAuthorOrAbove(req, problem)
                || (isPartner(req, problem) && getPartnerChildConfig(req, problem).getIsAllowedToManageGrading());
    }

    public boolean isAllowedToManageItems(Http.Request req, Problem problem) {
        return isAuthorOrAbove(req, problem)
                || (isPartner(req, problem) && getPartnerChildConfig(req, problem).getIsAllowedToManageItems());
    }

    public boolean isAllowedToUpdateItemInLanguage(Http.Request req, Problem problem, String language) {
        if (!isAllowedToManageItems(req, problem)) {
            return false;
        }
        if (isAuthorOrAbove(req, problem)) {
            return true;
        }
        if (!isPartner(req, problem)) {
            return false;
        }

        Set<String> allowedLanguages = getPartnerConfig(req, problem).getAllowedStatementLanguagesToUpdate();
        return allowedLanguages.isEmpty() || allowedLanguages.contains(language);
    }

    public Set<String> getAllowedStatementLanguagesToView(Http.Request req, Problem problem) {
        Map<String, StatementLanguageStatus> availableLanguages =
                problemStore.getStatementAvailableLanguages(getUserJid(req), problem.getJid());

        Set<String> allowedLanguages = Sets.newTreeSet();
        allowedLanguages.addAll(availableLanguages.entrySet().stream()
                .filter(e -> e.getValue() == StatementLanguageStatus.ENABLED)
                .map(e -> e.getKey())
                .collect(Collectors.toSet()));

        if (isPartner(req, problem)) {
            Set<String> allowedPartnerLanguages = getPartnerConfig(req, problem).getAllowedStatementLanguagesToView();
            if (!allowedPartnerLanguages.isEmpty()) {
                allowedLanguages.retainAll(allowedPartnerLanguages);
            }
            allowedLanguages.add(problemStore.getStatementDefaultLanguage(getUserJid(req), problem.getJid()));
        }

        return ImmutableSet.copyOf(allowedLanguages);
    }

    public Set<String> getAllowedStatementLanguagesToUpdate(Http.Request req, Problem problem) {
        Map<String, StatementLanguageStatus> availableLanguages =
                problemStore.getStatementAvailableLanguages(getUserJid(req), problem.getJid());

        Set<String> allowedLanguages = Sets.newTreeSet();
        allowedLanguages.addAll(availableLanguages.entrySet().stream()
                .filter(e -> e.getValue() == StatementLanguageStatus.ENABLED)
                .map(e -> e.getKey())
                .collect(Collectors.toSet()));

        if (isPartner(req, problem)) {
            Set<String> allowedPartnerLanguages = getPartnerConfig(req, problem).getAllowedStatementLanguagesToUpdate();
            if (!allowedPartnerLanguages.isEmpty()) {
                allowedLanguages.retainAll(allowedPartnerLanguages);
            }
            allowedLanguages.add(problemStore.getStatementDefaultLanguage(getUserJid(req), problem.getJid()));
        }

        return ImmutableSet.copyOf(allowedLanguages);
    }

    public Set<String> getAllowedEditorialLanguagesToView(Http.Request req, Problem problem) {
        Map<String, StatementLanguageStatus> availableLanguages =
                problemStore.getEditorialAvailableLanguages(getUserJid(req), problem.getJid());

        Set<String> allowedLanguages = Sets.newTreeSet();
        allowedLanguages.addAll(availableLanguages.entrySet().stream()
                .filter(e -> e.getValue() == StatementLanguageStatus.ENABLED)
                .map(e -> e.getKey())
                .collect(Collectors.toSet()));

        if (isPartner(req, problem)) {
            Set<String> allowedPartnerLanguages = getPartnerConfig(req, problem).getAllowedStatementLanguagesToView();
            if (!allowedPartnerLanguages.isEmpty()) {
                allowedLanguages.retainAll(allowedPartnerLanguages);
            }
            allowedLanguages.add(problemStore.getEditorialDefaultLanguage(getUserJid(req), problem.getJid()));
        }

        return ImmutableSet.copyOf(allowedLanguages);
    }

    public Set<String> getAllowedEditorialLanguagesToUpdate(Http.Request req, Problem problem) {
        Map<String, StatementLanguageStatus> availableLanguages =
                problemStore.getEditorialAvailableLanguages(getUserJid(req), problem.getJid());

        Set<String> allowedLanguages = Sets.newTreeSet();
        allowedLanguages.addAll(availableLanguages.entrySet().stream()
                .filter(e -> e.getValue() == StatementLanguageStatus.ENABLED)
                .map(e -> e.getKey())
                .collect(Collectors.toSet()));

        if (isPartner(req, problem)) {
            Set<String> allowedPartnerLanguages = getPartnerConfig(req, problem).getAllowedStatementLanguagesToUpdate();
            if (!allowedPartnerLanguages.isEmpty()) {
                allowedLanguages.retainAll(allowedPartnerLanguages);
            }
            allowedLanguages.add(problemStore.getEditorialDefaultLanguage(getUserJid(req), problem.getJid()));
        }

        return ImmutableSet.copyOf(allowedLanguages);
    }

    private ProblemPartnerConfig getPartnerConfig(Http.Request req, Problem problem) {
        return problemStore.findProblemPartnerByProblemJidAndPartnerJid(problem.getJid(), getUserJid(req))
                .getBaseConfig();
    }

    private ProblemPartnerChildConfig getPartnerChildConfig(Http.Request req, Problem problem) {
        return problemStore.findProblemPartnerByProblemJidAndPartnerJid(problem.getJid(), getUserJid(req))
                .getChildConfig();
    }
}
