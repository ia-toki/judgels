package judgels.michael.problem.bundle.item.config;

import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.bundle.StatementItemConfig;

public class StatementItemConfigAdapter implements ItemConfigAdapter {
    @Override
    public ItemConfigForm buildFormFromConfig(ItemConfig config) {
        ItemConfigForm form = new ItemConfigForm();
        form.statement = config.getStatement();
        return form;
    }

    @Override
    public ItemConfig buildConfigFromForm(ItemConfigForm form) {
        return new StatementItemConfig.Builder()
                .statement(form.statement)
                .build();
    }
}
