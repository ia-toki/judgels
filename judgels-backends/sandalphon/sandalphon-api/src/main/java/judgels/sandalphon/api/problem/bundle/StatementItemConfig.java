package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.function.Function;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableStatementItemConfig.class)
public abstract class StatementItemConfig implements ItemConfig {

    @Override
    public ItemConfig processDisplayText(Function<String, String> processor) {
        return new StatementItemConfig.Builder()
                .from(this)
                .statement(processor.apply(getStatement()))
                .build();
    }

    @Override
    public ItemConfig withoutGradingInfo() {
        return this;
    }

    public static class Builder extends ImmutableStatementItemConfig.Builder {}
}
