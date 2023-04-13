package judgels.sandalphon.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.bundle.MultipleChoiceItemConfig;

public class MultipleChoiceItemProcessor implements ItemProcessor {
    @Override
    public ItemConfig parseItemConfigFromString(ObjectMapper objectMapper, String json) throws IOException {
        return objectMapper.readValue(json, MultipleChoiceItemConfig.class);
    }

    @Override
    public Item replaceRenderUrls(Item item, String baseUrl, String problemJid) {
        MultipleChoiceItemConfig itemConfig = (MultipleChoiceItemConfig) item.getConfig();

        List<MultipleChoiceItemConfig.Choice> choices = itemConfig.getChoices().stream()
                .map(choice -> new MultipleChoiceItemConfig.Choice.Builder()
                        .from(choice)
                        .content(SandalphonUtils.replaceProblemRenderUrls(
                                choice.getContent(),
                                baseUrl,
                                problemJid))
                        .build())
                .collect(Collectors.toList());

        return new Item.Builder()
                .from(item)
                .config(new MultipleChoiceItemConfig.Builder()
                        .from(itemConfig)
                        .statement(
                                SandalphonUtils.replaceProblemRenderUrls(
                                        item.getConfig().getStatement(),
                                        baseUrl,
                                        problemJid))
                        .choices(choices)
                        .build())
                .build();
    }

    @Override
    public Item removeAnswerKey(Item item) {
        MultipleChoiceItemConfig itemConfig = (MultipleChoiceItemConfig) item.getConfig();

        List<MultipleChoiceItemConfig.Choice> choices = itemConfig.getChoices().stream()
                .map(choice -> new MultipleChoiceItemConfig.Choice.Builder()
                        .from(choice)
                        .isCorrect(Optional.empty())
                        .build())
                .collect(Collectors.toList());

        return new Item.Builder()
                .from(item)
                .config(new MultipleChoiceItemConfig.Builder()
                        .from(item.getConfig())
                        .choices(choices)
                        .build())
                .build();
    }
}
