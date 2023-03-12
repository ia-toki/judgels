package judgels.michael.resource;

import java.util.Map;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.resource.StatementLanguageStatus;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class ListStatementLanguagesView extends TemplateView {
    private final String basePath;
    private final Map<String, StatementLanguageStatus> availableLanguages;
    private final String defaultLanguage;

    public ListStatementLanguagesView(
            HtmlTemplate template,
            String basePath,
            Map<String, StatementLanguageStatus> availableLanguages,
            String defaultLanguage) {

        super("listStatementLanguagesView.ftl", template);
        this.basePath = basePath;
        this.availableLanguages = availableLanguages;
        this.defaultLanguage = defaultLanguage;
    }

    public String getBasePath() {
        return basePath;
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
}
