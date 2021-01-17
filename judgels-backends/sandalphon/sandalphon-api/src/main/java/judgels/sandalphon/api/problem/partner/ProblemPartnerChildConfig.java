package judgels.sandalphon.api.problem.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemPartnerChildConfig.class)
public interface ProblemPartnerChildConfig {
    boolean getIsAllowedToSubmit();

    @Value.Default
    default boolean getIsAllowedToManageGrading() {
        return false;
    }

    @Value.Default
    default boolean getIsAllowedToManageItems() {
        return false;
    }

    class Builder extends ImmutableProblemPartnerChildConfig.Builder {}
}
