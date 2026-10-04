package judgels.catalog.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.api.catalog.problem.bundle.EssayItemConfig;
import judgels.api.catalog.problem.bundle.Item;
import judgels.api.catalog.problem.bundle.ItemConfig;
import judgels.catalog.problem.ProblemUtils;

public class EssayItemProcessor implements ItemProcessor {
    @Override
    public ItemConfig parseItemConfigFromString(ObjectMapper objectMapper, String json) throws IOException {
        return objectMapper.readValue(json, EssayItemConfig.class);
    }

    @Override
    public Item replaceRenderUrls(Item item, String apiUrl, String problemJid) {
        return new Item.Builder()
                .from(item)
                .config(new EssayItemConfig.Builder()
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
        return item;
    }
}
