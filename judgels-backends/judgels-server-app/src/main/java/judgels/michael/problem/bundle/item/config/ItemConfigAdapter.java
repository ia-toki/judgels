package judgels.michael.problem.bundle.item.config;

import judgels.api.catalog.problem.bundle.ItemConfig;

public interface ItemConfigAdapter {
    ItemConfigForm buildFormFromConfig(ItemConfig config);
    ItemConfig buildConfigFromForm(ItemConfigForm form);
}
