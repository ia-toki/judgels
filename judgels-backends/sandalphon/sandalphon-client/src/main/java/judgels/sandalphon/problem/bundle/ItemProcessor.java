package judgels.sandalphon.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemConfig;

public interface ItemProcessor {
    ItemConfig parseItemConfigFromString(ObjectMapper objectMapper, String json) throws IOException;
    Item replaceRenderUrls(Item item, String baseUrl, String problemJid);
    Item removeAnswerKey(Item item);
}
