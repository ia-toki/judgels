package judgels.michael.problem.bundle.item.config;

import judgels.sandalphon.api.problem.bundle.EssayItemConfig;
import judgels.sandalphon.api.problem.bundle.ItemConfig;

public class EssayItemConfigAdapter implements ItemConfigAdapter {
    @Override
    public ItemConfigForm buildFormFromConfig(ItemConfig config) {
        ItemConfigForm form = new ItemConfigForm();
        form.statement = config.getStatement();

        EssayItemConfig cfg = (EssayItemConfig) config;
        form.score = cfg.getScore();
        return form;
    }

    @Override
    public ItemConfig buildConfigFromForm(ItemConfigForm form) {
        return new EssayItemConfig.Builder()
                .statement(form.statement)
                .score(form.score)
                .build();
    }
}
