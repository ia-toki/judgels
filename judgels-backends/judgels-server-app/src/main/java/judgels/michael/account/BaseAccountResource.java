package judgels.michael.account;

import judgels.jophiel.api.actor.Actor;
import judgels.michael.BaseResource;
import judgels.michael.template.HtmlTemplate;

public class BaseAccountResource extends BaseResource {
    protected HtmlTemplate newAccountsTemplate(Actor actor) {
        HtmlTemplate template = super.newTemplate(actor);
        template.setTitle("Accounts");
        template.setActiveSidebarMenu("accounts");
        template.addMainTab("users", "Users", "/accounts/users");
        template.addMainTab("roles", "Roles", "/accounts/roles");
        return template;
    }
}
