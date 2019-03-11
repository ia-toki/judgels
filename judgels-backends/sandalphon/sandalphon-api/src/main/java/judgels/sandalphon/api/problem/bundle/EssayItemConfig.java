package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableEssayItemConfig.class)
public interface EssayItemConfig extends ItemConfig {
    int getScore();

    class Builder extends ImmutableEssayItemConfig.Builder {}
}
