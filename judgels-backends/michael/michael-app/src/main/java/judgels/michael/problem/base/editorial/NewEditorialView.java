package judgels.michael.problem.base.editorial;

import java.util.Map;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class NewEditorialView extends TemplateView {
    private final boolean canEdit;

    public NewEditorialView(HtmlTemplate template, NewEditorialForm form, boolean canEdit) {
        super("newEditorialView.ftl", template, form);
        this.canEdit = canEdit;
    }

    public Map<String, String> getLanguages() {
        return WorldLanguageRegistry.getInstance().getLanguages();
    }

    public boolean getCanEdit() {
        return canEdit;
    }
}
