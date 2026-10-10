package judgels.api.catalog.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemConfig.class)
public interface ProblemConfig {
    boolean getCanEdit();
    boolean getCanManage();

    class Builder extends ImmutableProblemConfig.Builder {}
}
