package judgels.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.api.problem.bundle.Item;
import judgels.api.problem.bundle.ItemConfig;

public interface ItemProcessor {
    ItemConfig parseItemConfigFromString(ObjectMapper objectMapper, String json) throws IOException;
    Item replaceRenderUrls(Item item, String apiUrl, String problemJid);
    Item removeAnswerKey(Item item);
}
