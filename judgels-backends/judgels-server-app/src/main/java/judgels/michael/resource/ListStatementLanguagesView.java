package judgels.michael.resource;

import java.util.Map;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.resource.StatementLanguageStatus;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class ListStatementLanguagesView extends TemplateView {
    private final Map<String, StatementLanguageStatus> availableLanguages;
    private final String defaultLanguage;
    private final boolean canEdit;

    public ListStatementLanguagesView(
            HtmlTemplate template,
            Map<String, StatementLanguageStatus> availableLanguages,
            String defaultLanguage,
            boolean canEdit) {

        super("listStatementLanguagesView.ftl", template);
        this.availableLanguages = availableLanguages;
        this.defaultLanguage = defaultLanguage;
        this.canEdit = canEdit;
    }

    public Map<String, StatementLanguageStatus> getAvailableLanguages() {
        return availableLanguages;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public Map<String, String> getLanguages() {
        return WorldLanguageRegistry.getInstance().getLanguages();
    }

    public boolean getCanEdit() {
        return canEdit;
    }
}
