package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@JsonTypeName("MULTIPLE_CHOICE")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableMultipleChoiceItemConfig.class)
public interface MultipleChoiceItemConfig extends ItemConfig {
    double getScore();
    double getPenalty();
    List<Choice> getChoices();

    @Value.Immutable
    @JsonDeserialize(as = ImmutableChoice.class)
    interface Choice {
        String getAlias();
        String getContent();
        Optional<Boolean> getIsCorrect();

        class Builder extends ImmutableChoice.Builder {}
    }

    class Builder extends ImmutableMultipleChoiceItemConfig.Builder {}
}
