package judgels.gabriel.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLanguageRestriction.class)
public abstract class LanguageRestriction {
    @JsonProperty("allowedLanguageNames")
    public abstract Set<String> getAllowedLanguages();

    public static LanguageRestriction noRestriction() {
        return ImmutableLanguageRestriction.builder().build();
    }

    public static LanguageRestriction of(Set<String> allowedLanguages) {
        return ImmutableLanguageRestriction.builder().allowedLanguages(allowedLanguages).build();
    }
}
