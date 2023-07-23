package judgels.michael.account.user;

import java.time.Instant;
import java.util.Map;
import judgels.jophiel.api.user.User;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.persistence.api.Page;

public class ListUsersView extends TemplateView {
    private final Page<User> users;
    private final Map<String, Instant> lastSessionTimesMap;

    public ListUsersView(HtmlTemplate template, Page<User> users, Map<String, Instant> lastSessionTimesMap) {
        super("listUsersView.ftl", template);
        this.users = users;
        this.lastSessionTimesMap = lastSessionTimesMap;
    }

    public Page<User> getUsers() {
        return users;
    }

    public Map<String, Instant> getLastSessionTimesMap() {
        return lastSessionTimesMap;
    }
}
