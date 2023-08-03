package judgels.michael.account.role;

import java.util.List;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.role.UserWithRole;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class ListRolesView extends TemplateView {
    private final List<UserWithRole> userWithRoles;
    private final Map<String, Profile> profilesMap;

    public ListRolesView(HtmlTemplate template, List<UserWithRole> userWithRoles, Map<String, Profile> profilesMap) {
        super("listRolesView.ftl", template);
        this.userWithRoles = userWithRoles;
        this.profilesMap = profilesMap;
    }

    public List<UserWithRole> getUserWithRoles() {
        return userWithRoles;
    }

    public Map<String, Profile> getProfilesMap() {
        return profilesMap;
    }
}
