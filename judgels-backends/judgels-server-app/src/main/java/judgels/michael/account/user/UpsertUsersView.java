package judgels.michael.account.user;

import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class UpsertUsersView extends TemplateView {
    public UpsertUsersView(HtmlTemplate template, UpsertUsersForm form) {
        super("upsertUsersView.ftl", template, form);
    }
}
