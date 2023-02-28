package judgels.michael.problem.base;

import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class EditProblemView extends TemplateView {
    public EditProblemView(HtmlTemplate template, EditProblemForm form) {
        super("editProblemView.ftl", template, form);
    }
}
