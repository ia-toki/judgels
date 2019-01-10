package org.iatoki.judgels.sandalphon.problem.programming.grading;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class LanguageRestriction {
    private Set<String> allowedLanguageNames;

    public LanguageRestriction(Set<String> allowedLanguageNames) {
        this.allowedLanguageNames = allowedLanguageNames;
    }

    public static LanguageRestriction defaultRestriction() {
        return new LanguageRestriction(ImmutableSet.of());
    }

    public Set<String> getAllowedLanguageNames() {
        return allowedLanguageNames;
    }

    public boolean isAllowedAll() {
        return allowedLanguageNames.isEmpty();
    }
}
