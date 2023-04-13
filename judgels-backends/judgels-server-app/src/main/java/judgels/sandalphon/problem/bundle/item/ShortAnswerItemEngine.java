package judgels.sandalphon.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.bundle.ShortAnswerItemConfig;

public class ShortAnswerItemEngine implements ItemEngine {
    @Override
    public String getName() {
        return "Short Answer";
    }

    @Override
    public ItemConfig createDefaultConfig() {
        return new ShortAnswerItemConfig.Builder()
                .statement("")
                .score(1.0)
                .penalty(0.0)
                .inputValidationRegex(".*")
                .build();
    }

    @Override
    public ItemConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, ShortAnswerItemConfig.class);
    }

    @Override
    public double calculateScore(ItemConfig config, String answer) {
        ShortAnswerItemConfig cfg = (ShortAnswerItemConfig) config;
        if (answer.matches(cfg.getGradingRegex().orElse(""))) {
            return cfg.getScore();
        } else {
            return cfg.getPenalty();
        }
    }
}
