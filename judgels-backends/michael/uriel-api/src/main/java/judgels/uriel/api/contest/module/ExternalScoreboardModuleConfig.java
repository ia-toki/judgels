package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@JsonTypeName("EXTERNAL_SCOREBOARD")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableExternalScoreboardModuleConfig.class)
public interface ExternalScoreboardModuleConfig extends ModuleConfig {
    ExternalScoreboardModuleConfig DEFAULT = new Builder()
            .receiverUrl("http://localhost:9144/receive")
            .receiverSecret("secret")
            .build();

    String getReceiverUrl();
    String getReceiverSecret();

    class Builder extends ImmutableExternalScoreboardModuleConfig.Builder {}
}
