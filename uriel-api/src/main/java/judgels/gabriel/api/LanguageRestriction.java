package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLanguageRestriction.class)
public abstract class LanguageRestriction {
    public abstract Set<String> getAllowedLanguageNames();

    public static LanguageRestriction noRestriction() {
        return ImmutableLanguageRestriction.builder().build();
    }

    public static LanguageRestriction of(Set<String> allowedLanguageNames) {
        return ImmutableLanguageRestriction.builder().allowedLanguageNames(allowedLanguageNames).build();
    }
}
