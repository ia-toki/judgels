package judgels.michael.problem.base.editorial;

import java.util.Map;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class CreateEditorialView extends TemplateView {
    public CreateEditorialView(HtmlTemplate template, CreateEditorialForm form) {
        super("createEditorialView.ftl", template, form);
    }

    public Map<String, String> getLanguages() {
        return WorldLanguageRegistry.getInstance().getLanguages();
    }
}
