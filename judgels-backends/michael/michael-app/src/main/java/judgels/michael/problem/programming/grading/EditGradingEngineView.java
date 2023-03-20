package judgels.michael.problem.programming.grading;

import java.util.Map;
import judgels.gabriel.engines.GradingEngineRegistry;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class EditGradingEngineView extends TemplateView {
    private final boolean canEdit;

    public EditGradingEngineView(HtmlTemplate template, EditGradingEngineForm form, boolean canEdit) {
        super("editGradingEngineView.ftl", template, form);
        this.canEdit = canEdit;
    }

    public Map<String, String> getGradingEngines() {
        return GradingEngineRegistry.getInstance().getNamesMap();
    }

    public boolean getCanEdit() {
        return canEdit;
    }
}
