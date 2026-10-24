package judgels.api.catalog.problem.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemPartner.class)
public interface ProblemPartner {
    String getUserJid();

    class Builder extends ImmutableProblemPartner.Builder {}
}
