package judgels.michael.problem.base.editorial;

import java.util.Map;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class NewEditorialView extends TemplateView {
    public NewEditorialView(HtmlTemplate template, NewEditorialForm form) {
        super("newEditorialView.ftl", template, form);
    }

    public Map<String, String> getLanguages() {
        return WorldLanguageRegistry.getInstance().getLanguages();
    }
}
