package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableStatementItemConfig.class)
public interface StatementItemConfig extends ItemConfig {
    class Builder extends ImmutableStatementItemConfig.Builder {}
}
