package judgels.uriel.api.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUrielDump.class)
public interface UrielDump {
    Set<AdminRoleDump> getAdmins();
    Set<ContestDump> getContests();

    class Builder extends ImmutableUrielDump.Builder {}
}
