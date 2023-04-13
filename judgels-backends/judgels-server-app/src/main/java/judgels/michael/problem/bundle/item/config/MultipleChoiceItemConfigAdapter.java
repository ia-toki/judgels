package judgels.michael.problem.bundle.item.config;

import java.util.ArrayList;
import java.util.List;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.bundle.MultipleChoiceItemConfig;

public class MultipleChoiceItemConfigAdapter implements ItemConfigAdapter {
    @Override
    public ItemConfigForm buildFormFromConfig(ItemConfig config) {
        ItemConfigForm form = new ItemConfigForm();
        form.statement = config.getStatement();

        MultipleChoiceItemConfig cfg = (MultipleChoiceItemConfig) config;
        form.score = cfg.getScore();
        form.penalty = cfg.getPenalty();

        for (int i = 0; i < cfg.getChoices().size(); i++) {
            MultipleChoiceItemConfig.Choice choice = cfg.getChoices().get(i);
            form.choiceAliases.add(choice.getAlias());
            form.choiceContents.add(choice.getContent());

            if (choice.getIsCorrect().orElse(false)) {
                form.choiceIsCorrects.add(i);
            }
        }

        return form;
    }

    @Override
    public ItemConfig buildConfigFromForm(ItemConfigForm form) {
        List<MultipleChoiceItemConfig.Choice> choices = new ArrayList<>();

        for (int i = 0; i < form.choiceAliases.size(); i++) {
            if (form.choiceAliases.get(i).isEmpty()) {
                continue;
            }
            choices.add(new MultipleChoiceItemConfig.Choice.Builder()
                    .alias(form.choiceAliases.get(i))
                    .content(form.choiceContents.get(i))
                    .isCorrect(form.choiceIsCorrects.contains(i))
                    .build());
        }

        return new MultipleChoiceItemConfig.Builder()
                .statement(form.statement)
                .score(form.score)
                .penalty(form.penalty)
                .choices(choices)
                .build();
    }
}
