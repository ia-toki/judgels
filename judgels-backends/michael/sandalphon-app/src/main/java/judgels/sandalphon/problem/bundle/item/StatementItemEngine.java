package judgels.sandalphon.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.bundle.StatementItemConfig;

public class StatementItemEngine implements ItemEngine {
    @Override
    public String getName() {
        return "Statement";
    }

    @Override
    public ItemConfig createDefaultConfig() {
        return new StatementItemConfig.Builder()
                .statement("")
                .build();
    }

    @Override
    public ItemConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, StatementItemConfig.class);
    }

    @Override
    public double calculateScore(ItemConfig config, String answer) {
        throw new UnsupportedOperationException();
    }
}
