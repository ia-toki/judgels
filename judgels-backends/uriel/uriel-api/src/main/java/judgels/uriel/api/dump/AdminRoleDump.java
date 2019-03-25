package judgels.uriel.api.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.persistence.api.dump.UnmodifiableDump;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAdminRoleDump.class)
public interface AdminRoleDump extends UnmodifiableDump {
    String getUserJid();

    class Builder extends ImmutableAdminRoleDump.Builder {}
}
