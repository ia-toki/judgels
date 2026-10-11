package judgels.api.catalog.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemPermissions.class)
public interface ProblemPermissions {
    boolean getCanEdit();
    boolean getCanManage();
    Optional<String> getReasonNotAllowedToSubmit();

    class Builder extends ImmutableProblemPermissions.Builder {}
}
