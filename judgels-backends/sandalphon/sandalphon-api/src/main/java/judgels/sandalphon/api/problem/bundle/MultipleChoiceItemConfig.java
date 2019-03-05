package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableMultipleChoiceItemConfig.class)
public abstract class MultipleChoiceItemConfig implements ItemConfig {
    public abstract int getScore();
    public abstract int getPenalty();
    public abstract List<Choice> getChoices();

    @Value.Immutable
    @JsonDeserialize(as = ImmutableChoice.class)
    public interface Choice {
        String getAlias();
        String getContent();
        Optional<Boolean> getIsCorrect();

        class Builder extends ImmutableChoice.Builder {}
    }

    @Override
    public ItemConfig processDisplayText(Function<String, String> processor) {
        List<Choice> processedChoices = getChoices().stream()
                .map(choice -> new Choice.Builder()
                        .from(choice)
                        .content(processor.apply(choice.getContent()))
                        .build())
                .collect(Collectors.toList());
        return new MultipleChoiceItemConfig.Builder()
                .from(this)
                .statement(processor.apply(getStatement()))
                .choices(processedChoices)
                .build();
    }

    @Override
    public ItemConfig withoutGradingInfo() {
        List<Choice> choicesWithoutGradingInfo = getChoices().stream()
                .map(choice -> new Choice.Builder().from(choice).isCorrect(Optional.empty()).build())
                .collect(Collectors.toList());
        return new MultipleChoiceItemConfig.Builder()
                .from(this)
                .choices(choicesWithoutGradingInfo)
                .build();
    }

    public static class Builder extends ImmutableMultipleChoiceItemConfig.Builder {}
}
