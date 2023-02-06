package judgels.sandalphon.api.problem.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemPartner.class)
public interface ProblemPartner {
    long getId();
    String getProblemJid();
    String getUserJid();
    ProblemPartnerConfig getBaseConfig();
    ProblemPartnerChildConfig getChildConfig();

    class Builder extends ImmutableProblemPartner.Builder {}
}
