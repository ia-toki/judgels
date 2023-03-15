package judgels.sandalphon.api.problem.partner;

import org.immutables.value.Value;

@Value.Immutable
public interface ProblemPartnerV2 {
    String getUserJid();
    PartnerPermission getPermission();

    class Builder extends ImmutableProblemPartnerV2.Builder {}
}
