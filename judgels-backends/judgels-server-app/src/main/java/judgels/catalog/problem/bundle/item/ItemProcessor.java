package judgels.catalog.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.api.catalog.problem.bundle.Item;
import judgels.api.catalog.problem.bundle.ItemConfig;

public interface ItemProcessor {
    ItemConfig parseItemConfigFromString(ObjectMapper objectMapper, String json) throws IOException;
    Item replaceRenderUrls(Item item, String apiUrl, String problemJid);
    Item removeAnswerKey(Item item);
}
