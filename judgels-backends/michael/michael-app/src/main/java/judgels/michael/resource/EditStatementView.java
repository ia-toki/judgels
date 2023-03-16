package judgels.michael.resource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class EditStatementView extends TemplateView {
    private final String language;
    private final Set<String> enabledLanguages;
    private final boolean canEdit;

    public EditStatementView(
            HtmlTemplate template,
            EditStatementForm form,
            String language,
            Set<String> enabledLanguages,
            boolean canEdit) {

        super("editStatementView.ftl", template, form);
        this.language = language;
        this.enabledLanguages = enabledLanguages;
        this.canEdit = canEdit;
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
