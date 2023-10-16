package judgels.michael.index;

import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class LoginView extends TemplateView {
    private boolean hasGoogleAuth;

    public LoginView(HtmlTemplate template, LoginForm form, boolean hasGoogleAuth) {
        super("loginView.ftl", template, form);
        this.hasGoogleAuth = hasGoogleAuth;
    }

    public boolean getHasGoogleAuth() {
        return hasGoogleAuth;
    }
}
