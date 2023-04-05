package judgels.sandalphon.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.sandalphon.api.problem.bundle.EssayItemConfig;
import judgels.sandalphon.api.problem.bundle.ItemConfig;

public class EssayItemEngine implements ItemEngine {
    @Override
    public String getName() {
        return "Essay";
    }

    @Override
    public ItemConfig createDefaultConfig() {
        return new EssayItemConfig.Builder()
                .statement("")
                .score(1.0)
                .build();
    }

    @Override
    public ItemConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, EssayItemConfig.class);
    }

    @Override
    public double calculateScore(ItemConfig config, String answer) {
        return 0; // Essay items are to be graded manually
    }
}
