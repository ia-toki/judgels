package judgels.michael.problem.programming.statement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.OutputOnlyOverrides;
import judgels.gabriel.engines.outputonly.OutputOnlyGradingConfig;
import judgels.gabriel.engines.outputonly.OutputOnlyWithSubtasksGradingConfig;
import judgels.gabriel.languages.GradingLanguageRegistry;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class ViewStatementView extends TemplateView {
    private final ProblemStatement statement;
    private final String language;
    private final Set<String> enabledLanguages;
    private final GradingConfig gradingConfig;
    private final String gradingEngine;
    private final Set<String> allowedGradingLanguages;
    private final String reasonNotAllowedToSubmit;

    public ViewStatementView(
            HtmlTemplate template,
            ProblemStatement statement,
            String language,
            Set<String> enabledLanguages,
            GradingConfig gradingConfig,
            String gradingEngine,
            Set<String> allowedGradingLanguages,
            String reasonNotAllowedToSubmit) {

        super("viewStatementView.ftl", template);
        this.statement = statement;
        this.language = language;
        this.enabledLanguages = enabledLanguages;
        this.gradingConfig = gradingConfig;
        this.gradingEngine = gradingEngine;
        this.allowedGradingLanguages = allowedGradingLanguages;
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

    public GradingConfig getGradingConfig() {
        return gradingConfig;
    }

    public boolean getIsOutputOnly() {
        return gradingConfig instanceof OutputOnlyGradingConfig || gradingConfig instanceof OutputOnlyWithSubtasksGradingConfig;
    }

    public String getOutputOnlyGradingLanguage() {
        return OutputOnlyOverrides.KEY;
    }

    public String getGradingEngine() {
        return gradingEngine;
    }

    public String getSourceKeys() {
        return String.join(",", gradingConfig.getSourceFileFields().keySet());
    }

    public Map<String, String> getAllowedGradingLanguages() {
        Map<String, String> languages = new LinkedHashMap<>();
        for (String lang : allowedGradingLanguages) {
            languages.put(lang, GradingLanguageRegistry.getInstance().getLanguages().get(lang));
        }
        return languages;
    }

    public String getReasonNotAllowedToSubmit() {
        return reasonNotAllowedToSubmit;
    }
}
