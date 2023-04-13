package judgels.michael.problem.bundle.statement;

import static java.util.stream.Collectors.toList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class ViewStatementView extends TemplateView {
    private final ProblemStatement statement;
    private final List<BundleItem> items;
    private final List<ItemConfig> itemConfigs;
    private final String language;
    private final Set<String> enabledLanguages;
    private final String reasonNotAllowedToSubmit;
    private final boolean canSubmit;

    public ViewStatementView(
            HtmlTemplate template,
            ProblemStatement statement,
            List<BundleItem> items,
            List<ItemConfig> itemConfigs,
            String language,
            Set<String> enabledLanguages,
            String reasonNotAllowedToSubmit,
            boolean canSubmit) {

        super("viewStatementView.ftl", template);
        this.statement = statement;
        this.items = items;
        this.itemConfigs = itemConfigs;
        this.language = language;
        this.enabledLanguages = enabledLanguages;
        this.reasonNotAllowedToSubmit = reasonNotAllowedToSubmit;
        this.canSubmit = canSubmit;
    }

    public ProblemStatement getStatement() {
        return statement;
    }

    public List<BundleItem> getItems() {
        return items;
    }

    public List<String> getItemTypes() {
        return items.stream().map(item -> item.getType().name()).collect(toList());
    }

    public List<ItemConfig> getItemConfigs() {
        return itemConfigs;
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

    public String getReasonNotAllowedToSubmit() {
        return reasonNotAllowedToSubmit;
    }

    public boolean isCanSubmit() {
        return canSubmit;
    }
}
