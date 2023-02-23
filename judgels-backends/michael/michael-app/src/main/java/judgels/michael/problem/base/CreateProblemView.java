package judgels.michael.problem.base;

import java.util.LinkedHashMap;
import java.util.Map;
import judgels.gabriel.engines.GradingEngineRegistry;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class CreateProblemView extends TemplateView {
    public CreateProblemView(HtmlTemplate template, CreateProblemForm form) {
        super("createProblemView.ftl", template, form);
    }

    public Map<String, String> getLanguages() {
        return WorldLanguageRegistry.getInstance().getLanguages();
    }

    public Map<String, String> getGradingEngines() {
        Map<String, String> engines = new LinkedHashMap<>(GradingEngineRegistry.getInstance().getNamesMap());
        engines.put("Bundle", "Bundle");
        return engines;
    }
}
