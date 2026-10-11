package judgels.api.catalog.problem.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.catalog.PartnerPermission;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemPartnerData.class)
public interface ProblemPartnerData {
    String getUsername();
    PartnerPermission getPermission();

    class Builder extends ImmutableProblemPartnerData.Builder {}
}
