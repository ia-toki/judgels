package judgels.michael.account.role;

import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class EditRolesView extends TemplateView {
    public EditRolesView(HtmlTemplate template, EditRolesForm form) {
        super("editRolesView.ftl", template, form);
    }
}
