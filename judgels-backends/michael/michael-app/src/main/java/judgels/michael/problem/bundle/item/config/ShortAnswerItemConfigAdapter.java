package judgels.michael.problem.bundle.item.config;

import java.util.Optional;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.bundle.ShortAnswerItemConfig;

public class ShortAnswerItemConfigAdapter implements ItemConfigAdapter {
    @Override
    public ItemConfigForm buildFormFromConfig(ItemConfig config) {
        ItemConfigForm form = new ItemConfigForm();
        form.statement = config.getStatement();

        ShortAnswerItemConfig cfg = (ShortAnswerItemConfig) config;
        form.score = cfg.getScore();
        form.penalty = cfg.getPenalty();
        form.inputValidationRegex = cfg.getInputValidationRegex();
        form.gradingRegex = cfg.getGradingRegex().orElse("");

        return form;
    }

    @Override
    public ItemConfig buildConfigFromForm(ItemConfigForm form) {
        return new ShortAnswerItemConfig.Builder()
                .statement(form.statement)
                .score(form.score)
                .penalty(form.penalty)
                .inputValidationRegex(form.inputValidationRegex)
                .gradingRegex(form.gradingRegex.isEmpty() ? Optional.empty() : Optional.of(form.gradingRegex))
                .build();
    }
}
