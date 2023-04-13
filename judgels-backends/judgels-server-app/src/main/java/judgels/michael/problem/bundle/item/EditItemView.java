package judgels.michael.problem.bundle.item;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import judgels.michael.problem.bundle.item.config.ItemConfigForm;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.problem.bundle.item.ItemEngineRegistry;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class EditItemView extends TemplateView {
    private final BundleItem item;
    private final boolean canEdit;
    private final String language;
    private final Set<String> enabledLanguages;

    public EditItemView(
            BundleItem item,
            HtmlTemplate template,
            ItemConfigForm form,
            String language,
            Set<String> enabledLanguages,
            boolean canEdit) {

        super("editItemView.ftl", template, form);
        this.item = item;
        this.language = language;
        this.enabledLanguages = enabledLanguages;
        this.canEdit = canEdit;
    }

    public BundleItem getItem() {
        return item;
    }

    public String getItemType() {
        return item.getType().name();
    }

    public String getItemTypeName() {
        return ItemEngineRegistry.getByType(item.getType()).getName();
    }

    public String getLanguage() {
        return language;
    }

    public Map<String, String> getEnabledLanguages() {
        Map<String, String> languages = new LinkedHashMap<>();
        for (String lang : enabledLanguages) {
            languages.put(lang, WorldLanguageRegistry.getInstance().getLanguages().get(lang));
        }
        return languages;
    }

    public boolean getCanEdit() {
        return canEdit;
    }
}
