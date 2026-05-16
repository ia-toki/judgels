package judgels.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.api.problem.bundle.EssayItemConfig;
import judgels.api.problem.bundle.Item;
import judgels.api.problem.bundle.ItemConfig;
import judgels.sandalphon.SandalphonUtils;

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
                                SandalphonUtils.replaceProblemRenderUrls(
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
