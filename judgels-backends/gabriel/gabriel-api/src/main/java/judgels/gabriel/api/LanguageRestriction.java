package judgels.gabriel.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLanguageRestriction.class)
public interface LanguageRestriction {
    Set<String> getAllowedLanguageNames();

    @JsonIgnore
    default boolean isAllowedAll() {
        return getAllowedLanguageNames().isEmpty();
    }

    static LanguageRestriction noRestriction() {
        return ImmutableLanguageRestriction.builder().build();
    }

    static LanguageRestriction of(Set<String> allowedLanguageNames) {
        return ImmutableLanguageRestriction.builder().allowedLanguageNames(allowedLanguageNames).build();
    }
}
