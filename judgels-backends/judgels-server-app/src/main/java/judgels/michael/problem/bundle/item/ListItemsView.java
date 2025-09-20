package judgels.michael.problem.bundle.item;

import java.util.List;
import java.util.Map;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.problem.bundle.item.ItemEngineRegistry;

public class ListItemsView extends TemplateView {
    private final List<BundleItem> items;
    private final Map<String, ItemConfig> itemConfigs;
    private final boolean canEdit;

    public ListItemsView(
            HtmlTemplate template,
            List<BundleItem> items,
            Map<String, ItemConfig> itemConfigs,
            boolean canEdit) {

        super("listItemsView.ftl", template);
        this.items = items;
        this.itemConfigs = itemConfigs;
        this.canEdit = canEdit;
    }

    public List<BundleItem> getItems() {
        return items;
    }

    public Map<String, ItemConfig> getItemConfigs() {
        return itemConfigs;
    }

    public Map<String, String> getItemTypes() {
        return ItemEngineRegistry.getTypes();
    }

    public boolean getCanEdit() {
        return canEdit;
    }
}
