package judgels.michael.problem.editorial;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.problem.ProblemEditorial;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class ViewEditorialView extends TemplateView {
    private final ProblemEditorial editorial;
    private final String language;
    private final Set<String> enabledLanguages;

    public ViewEditorialView(
            HtmlTemplate template,
            ProblemEditorial editorial,
            String language,
            Set<String> enabledLanguages) {

        super("viewEditorialView.ftl", template);
        this.editorial = editorial;
        this.language = language;
        this.enabledLanguages = enabledLanguages;
    }

    public ProblemEditorial getEditorial() {
        return editorial;
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
}
