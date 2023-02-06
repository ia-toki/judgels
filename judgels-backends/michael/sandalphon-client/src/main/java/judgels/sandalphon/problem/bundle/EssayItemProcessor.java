package judgels.sandalphon.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.problem.bundle.EssayItemConfig;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemConfig;

public class EssayItemProcessor implements ItemProcessor {
    @Override
    public ItemConfig parseItemConfigFromString(ObjectMapper objectMapper, String json) throws IOException {
        return objectMapper.readValue(json, EssayItemConfig.class);
    }

    @Override
    public Item replaceRenderUrls(Item item, String baseUrl, String problemJid) {
        return new Item.Builder()
                .from(item)
                .config(new EssayItemConfig.Builder()
                        .from(item.getConfig())
                        .statement(
                                SandalphonUtils.replaceProblemRenderUrls(
                                        item.getConfig().getStatement(),
                                        baseUrl,
                                        problemJid))
                        .build())
                .build();
    }

    @Override
    public Item removeAnswerKey(Item item) {
        return item;
    }
}
