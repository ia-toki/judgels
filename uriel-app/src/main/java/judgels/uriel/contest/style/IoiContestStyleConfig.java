package judgels.uriel.contest.style;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableIoiContestStyleConfig.class)
public interface IoiContestStyleConfig {
    LanguageRestriction getLanguageRestriction();

    @Value.Default
    default boolean getUsingLastAffectingPenalty() {
        return false;
    }

    class Builder extends ImmutableIoiContestStyleConfig.Builder {}
}
