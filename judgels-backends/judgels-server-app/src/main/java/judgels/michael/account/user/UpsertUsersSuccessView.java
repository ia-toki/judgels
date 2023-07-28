package judgels.michael.account.user;

import java.util.List;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class UpsertUsersSuccessView extends TemplateView {
    private final List<String> createdUsernames;
    private final List<String> updatedUsernames;

    public UpsertUsersSuccessView(
            HtmlTemplate template,
            List<String> createdUsernames,
            List<String> updatedUsernames) {

        super("upsertUsersSuccessView.ftl", template);
        this.createdUsernames = createdUsernames;
        this.updatedUsernames = updatedUsernames;
    }

    public List<String> getCreatedUsernames() {
        return createdUsernames;
    }

    public List<String> getUpdatedUsernames() {
        return updatedUsernames;
    }
}
