package judgels.sandalphon.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.bundle.MultipleChoiceItemConfig;

public class MultipleChoiceItemEngine implements ItemEngine {
    @Override
    public String getName() {
        return "Multiple Choice";
    }

    @Override
    public ItemConfig createDefaultConfig() {
        return new MultipleChoiceItemConfig.Builder()
                .statement("")
                .score(4.0)
                .penalty(-1.0)
                .choices(List.of(
                        new MultipleChoiceItemConfig.Choice.Builder().alias("a").content("").build(),
                        new MultipleChoiceItemConfig.Choice.Builder().alias("b").content("").build(),
                        new MultipleChoiceItemConfig.Choice.Builder().alias("c").content("").build(),
                        new MultipleChoiceItemConfig.Choice.Builder().alias("d").content("").build(),
                        new MultipleChoiceItemConfig.Choice.Builder().alias("e").content("").build()))
                .build();
    }

    @Override
    public ItemConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, MultipleChoiceItemConfig.class);
    }

    @Override
    public double calculateScore(ItemConfig config, String answer) {
        MultipleChoiceItemConfig cfg = (MultipleChoiceItemConfig) config;
        for (MultipleChoiceItemConfig.Choice itemChoice : cfg.getChoices()) {
            if (itemChoice.getAlias().equals(answer)) {
                if (itemChoice.getIsCorrect().orElse(false)) {
                    return cfg.getScore();
                } else {
                    return cfg.getPenalty();
                }
            }
        }
        return 0;
    }
}
