package judgels.michael.problem.programming.grading;

import java.util.Map;
import judgels.gabriel.languages.GradingLanguageRegistry;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class EditGradingLanguageRestrictionView extends TemplateView {
    private final boolean canEdit;

    public EditGradingLanguageRestrictionView(
            HtmlTemplate template,
            EditGradingLanguageRestrictionForm form,
            boolean canEdit) {

        super("editGradingLanguageRestrictionView.ftl", template, form);
        this.canEdit = canEdit;
    }

    public Map<String, String> getLanguages() {
        return GradingLanguageRegistry.getInstance().getVisibleLanguages();
    }

    public boolean getCanEdit() {
        return canEdit;
    }
}
