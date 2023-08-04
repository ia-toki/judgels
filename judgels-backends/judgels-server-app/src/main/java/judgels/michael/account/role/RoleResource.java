package judgels.michael.account.role;

import static judgels.service.ServiceUtils.checkAllowed;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.role.UserRole;
import judgels.jophiel.api.user.role.UserWithRole;
import judgels.jophiel.profile.ProfileStore;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.role.UserRoleStore;
import judgels.michael.account.BaseAccountResource;
import judgels.michael.template.HtmlTemplate;
import liquibase.util.csv.CSVReader;

@Path("/accounts/roles")
public class RoleResource extends BaseAccountResource {
    @Inject protected UserStore userStore;
    @Inject protected UserRoleStore userRoleStore;
    @Inject protected ProfileStore profileStore;

    @Inject public RoleResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listRoles(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);
        checkAllowed(userRoleChecker.canAdminister(actor.getUserJid()));

        List<UserWithRole> userWithRoles = userRoleStore.getRoles();

        var userJids = Lists.transform(userWithRoles, UserWithRole::getUserJid);
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        HtmlTemplate template = newRolesTemplate(actor);
        template.setActiveSecondaryTab("view");
        return new ListRolesView(template, userWithRoles, profilesMap);
    }

    @GET
    @Path("/edit")
    @UnitOfWork(readOnly = true)
    public View editRoles(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);
        checkAllowed(userRoleChecker.canAdminister(actor.getUserJid()));

        List<UserWithRole> userWithRoles = userRoleStore.getRoles();

        var userJids = Lists.transform(userWithRoles, UserWithRole::getUserJid);
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        EditRolesForm form = new EditRolesForm();
        form.csv = toCsv(userWithRoles, profilesMap);

        return renderEditRoles(actor, form);
    }

    @POST
    @Path("/edit")
    @UnitOfWork
    public Response updateRoles(@Context HttpServletRequest req, @BeanParam EditRolesForm form) {
        Actor actor = actorChecker.check(req);
        checkAllowed(userRoleChecker.canAdminister(actor.getUserJid()));

        Optional<Map<String, UserRole>> usernameToRoleMap = fromCsv(form.csv);
        if (usernameToRoleMap.isEmpty()) {
            form.globalError = "Invalid CSV format.";
            return ok(renderEditRoles(actor, form));
        }

        var userJids = usernameToRoleMap.get().keySet();
        Map<String, String> usernameToJidMap = userStore.translateUsernamesToJids(userJids);

        Map<String, UserRole> userJidToRoleMap = new HashMap<>();
        for (var entry : usernameToRoleMap.get().entrySet()) {
            if (usernameToJidMap.containsKey(entry.getKey())) {
                userJidToRoleMap.put(usernameToJidMap.get(entry.getKey()), entry.getValue());
            }
        }
        userRoleStore.setRoles(userJidToRoleMap);

        return redirect("/accounts/roles");
    }

    private EditRolesView renderEditRoles(Actor actor, EditRolesForm form) {
        HtmlTemplate template = newRolesTemplate(actor);
        template.setActiveSecondaryTab("edit");
        return new EditRolesView(template, form);
    }

    private HtmlTemplate newRolesTemplate(Actor actor) {
        HtmlTemplate template = super.newAccountsTemplate(actor);
        template.setTitle("Roles");
        template.setActiveMainTab("roles");
        template.addSecondaryTab("view", "View", "/accounts/roles");
        template.addSecondaryTab("edit", "Edit", "/accounts/roles/edit");
        return template;
    }

    private static String toCsv(List<UserWithRole> userWithRoles, Map<String, Profile> profilesMap) {
        List<String> rows = new ArrayList<>();
        for (UserWithRole userWithRole : userWithRoles) {
            List<String> row = new ArrayList<>();
            row.add(profilesMap.get(userWithRole.getUserJid()).getUsername());
            row.add(userWithRole.getRole().getJophiel().orElse(""));
            row.add(userWithRole.getRole().getSandalphon().orElse(""));
            row.add(userWithRole.getRole().getUriel().orElse(""));
            row.add(userWithRole.getRole().getJerahmeel().orElse(""));
            rows.add(String.join(",", row));
        }
        return String.join("\r\n", rows);
    }

    private static Optional<Map<String, UserRole>> fromCsv(String csv) {
        Map<String, UserRole> usernameToRoleMap = new HashMap<>();

        CSVReader reader = new CSVReader(new StringReader(csv));

        while (true) {
            String[] tokens;
            try {
                tokens = reader.readNext();
            } catch (IOException e) {
                tokens = new String[0];
            }
            if (tokens == null) {
                break;
            }

            if (tokens.length == 0) {
                continue;
            }
            if (tokens.length != 5) {
                return Optional.empty();
            }

            String username = tokens[0].trim();
            if (username.isEmpty()) {
                return Optional.empty();
            }

            UserRole.Builder role = new UserRole.Builder();

            String jophiel = tokens[1].trim();
            if (!jophiel.isEmpty()) {
                if (jophiel.equals("ADMIN")) {
                    role.jophiel(jophiel);
                } else {
                    return Optional.empty();
                }
            }

            String sandalphon = tokens[2].trim();
            if (!sandalphon.isEmpty()) {
                if (sandalphon.equals("ADMIN")) {
                    role.sandalphon(sandalphon);
                } else {
                    return Optional.empty();
                }
            }

            String uriel = tokens[3].trim();
            if (!uriel.isEmpty()) {
                if (uriel.equals("ADMIN")) {
                    role.uriel(uriel);
                } else {
                    return Optional.empty();
                }
            }

            String jerahmeel = tokens[4].trim();
            if (!jerahmeel.isEmpty()) {
                if (jerahmeel.equals("ADMIN")) {
                    role.jerahmeel(jerahmeel);
                } else {
                    return Optional.empty();
                }
            }

            usernameToRoleMap.put(username, role.build());
        }
        return Optional.of(usernameToRoleMap);
    }
}
