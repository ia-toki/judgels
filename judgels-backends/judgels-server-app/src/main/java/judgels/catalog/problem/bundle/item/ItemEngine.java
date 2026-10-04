package judgels.catalog.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.api.catalog.problem.bundle.ItemConfig;

public interface ItemEngine {
    String getName();
    ItemConfig createDefaultConfig();
    ItemConfig parseConfig(ObjectMapper mapper, String json) throws IOException;
    double calculateScore(ItemConfig config, String answer);
}
