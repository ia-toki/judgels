package judgels.michael.problem.bundle.statement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class ViewStatementView extends TemplateView {
    private final ProblemStatement statement;
    private final String language;
    private final Set<String> enabledLanguages;
    private final String reasonNotAllowedToSubmit;

    public ViewStatementView(
            HtmlTemplate template,
            ProblemStatement statement,
            String language,
            Set<String> enabledLanguages,
            String reasonNotAllowedToSubmit) {

        super("viewStatementView.ftl", template);
        this.statement = statement;
        this.language = language;
        this.enabledLanguages = enabledLanguages;
        this.reasonNotAllowedToSubmit = reasonNotAllowedToSubmit;
    }

    public ProblemStatement getStatement() {
        return statement;
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
}
