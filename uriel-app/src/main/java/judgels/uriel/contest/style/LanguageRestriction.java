package judgels.uriel.contest.style;

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
}
