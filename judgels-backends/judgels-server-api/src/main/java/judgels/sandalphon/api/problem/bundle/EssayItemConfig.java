package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@JsonTypeName("ESSAY")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableEssayItemConfig.class)
public interface EssayItemConfig extends ItemConfig {
    double getScore();

    class Builder extends ImmutableEssayItemConfig.Builder {}
}
