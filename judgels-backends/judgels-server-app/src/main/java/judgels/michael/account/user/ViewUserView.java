package judgels.michael.account.user;

import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class ViewUserView extends TemplateView {
    private final User user;
    private final UserInfo info;

    public ViewUserView(HtmlTemplate template, User user, UserInfo info) {
        super("viewUserView.ftl", template);
        this.user = user;
        this.info = info;
    }

    public User getUser() {
        return user;
    }

    public UserInfo getInfo() {
        return info;
    }
}
