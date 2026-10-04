package judgels.catalog.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import judgels.api.catalog.problem.bundle.Item;
import judgels.api.catalog.problem.bundle.ItemConfig;
import judgels.api.catalog.problem.bundle.ShortAnswerItemConfig;
import judgels.catalog.problem.ProblemUtils;

public class ShortAnswerItemProcessor implements ItemProcessor {
    @Override
    public ItemConfig parseItemConfigFromString(ObjectMapper objectMapper, String json) throws IOException {
        return objectMapper.readValue(json, ShortAnswerItemConfig.class);
    }

    @Override
    public Item replaceRenderUrls(Item item, String apiUrl, String problemJid) {
        return new Item.Builder()
                .from(item)
                .config(new ShortAnswerItemConfig.Builder()
                        .from(item.getConfig())
                        .statement(
                                ProblemUtils.replaceProblemRenderUrls(
                                        item.getConfig().getStatement(),
                                        apiUrl,
                                        problemJid))
                        .build())
                .build();
    }

    @Override
    public Item removeAnswerKey(Item item) {
        return new Item.Builder()
                .from(item)
                .config(new ShortAnswerItemConfig.Builder()
                        .from(item.getConfig())
                        .gradingRegex(Optional.empty())
                        .build())
                .build();
    }
}
