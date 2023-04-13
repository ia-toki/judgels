package judgels.michael.index;

import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class LoginView extends TemplateView {
    public LoginView(HtmlTemplate template, LoginForm form) {
        super("loginView.ftl", template, form);
    }
}
