package judgels.gabriel.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.HashSet;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLanguageRestriction.class)
public interface LanguageRestriction {
    @JsonProperty("allowedLanguageNames")
    Set<String> getAllowedLanguages();

    @JsonIgnore
    default boolean isAllowedAll() {
        return getAllowedLanguages().isEmpty();
    }

    static LanguageRestriction noRestriction() {
        return ImmutableLanguageRestriction.builder().build();
    }

    static LanguageRestriction of(Set<String> allowedLanguageNames) {
        return ImmutableLanguageRestriction.builder().allowedLanguages(allowedLanguageNames).build();
    }

    static LanguageRestriction combine(LanguageRestriction r1, LanguageRestriction r2) {
        if (r1.isAllowedAll()) {
            return r2;
        }
        if (r2.isAllowedAll()) {
            return r1;
        }
        Set<String> result = new HashSet<>(r1.getAllowedLanguages());
        result.retainAll(r2.getAllowedLanguages());
        return LanguageRestriction.of(result);
    }
}
