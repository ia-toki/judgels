package judgels.api.user.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRole.class)
public interface UserRole {
    Optional<String> getAccount();
    Optional<String> getProblem();
    Optional<String> getContest();
    Optional<String> getTraining();

    class Builder extends ImmutableUserRole.Builder {}
}
