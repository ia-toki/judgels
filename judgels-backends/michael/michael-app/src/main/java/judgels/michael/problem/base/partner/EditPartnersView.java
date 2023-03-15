package judgels.michael.problem.base.partner;

import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class EditPartnersView extends TemplateView {
    public EditPartnersView(HtmlTemplate template, EditPartnersForm form) {
        super("editPartnersView.ftl", template, form);
    }
}
