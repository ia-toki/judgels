package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.function.Function;

public interface ItemConfig {
    String getStatement();
    ItemConfig processDisplayText(Function<String, String> processor);
    ItemConfig withoutGradingInfo();

    static ItemConfig fromString(ObjectMapper objectMapper, ItemType itemType, String string) throws IOException {
        if (itemType == ItemType.STATEMENT) {
            return objectMapper.readValue(string, StatementItemConfig.class);
        } else if (itemType == ItemType.MULTIPLE_CHOICE) {
            return objectMapper.readValue(string, MultipleChoiceItemConfig.class);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
