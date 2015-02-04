package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableSet;
import org.iatoki.judgels.gabriel.GradingLanguage;

import java.util.Set;

public final class LanguageRestriction {
    private final Set<GradingLanguage> languages;
    private final Type type;

    public LanguageRestriction(Set<GradingLanguage> languages, Type type) {
        this.languages = languages;
        this.type = type;
    }

    public static LanguageRestriction noRestriction() {
        return new LanguageRestriction(ImmutableSet.of(), Type.DISALLOW);
    }

    public Set<GradingLanguage> getLanguages() {
        return languages;
    }

    public Type getType() {
        return type;
    }

    static enum Type {
        ALLOW,
        DISALLOW
    }
}
