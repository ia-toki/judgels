package judgels.api.catalog.problem.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemPartnerData.class)
public interface ProblemPartnerData {
    String getUsername();

    class Builder extends ImmutableProblemPartnerData.Builder {}
}
