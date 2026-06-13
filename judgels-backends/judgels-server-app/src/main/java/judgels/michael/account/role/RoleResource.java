package judgels.michael.account.role;

import static judgels.service.ServiceUtils.checkAllowed;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.common.View;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.api.actor.Actor;
import judgels.api.profile.Profile;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserWithRole;
import judgels.michael.account.BaseAccountResource;
import judgels.michael.template.HtmlTemplate;
import judgels.profile.ProfileStore;
import judgels.user.UserStore;
import judgels.user.role.UserRoleStore;
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
            row.add(userWithRole.getRole().getAccount().orElse(""));
            row.add(userWithRole.getRole().getProblem().orElse(""));
            row.add(userWithRole.getRole().getContest().orElse(""));
            row.add(userWithRole.getRole().getTraining().orElse(""));
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

            String account = tokens[1].trim();
            if (!account.isEmpty()) {
                if (account.equals("ADMIN")) {
                    role.account(account);
                } else {
                    return Optional.empty();
                }
            }

            String problem = tokens[2].trim();
            if (!problem.isEmpty()) {
                if (problem.equals("ADMIN")) {
                    role.problem(problem);
                } else {
                    return Optional.empty();
                }
            }

            String contest = tokens[3].trim();
            if (!contest.isEmpty()) {
                if (contest.equals("ADMIN")) {
                    role.contest(contest);
                } else {
                    return Optional.empty();
                }
            }

            String training = tokens[4].trim();
            if (!training.isEmpty()) {
                if (training.equals("ADMIN")) {
                    role.training(training);
                } else {
                    return Optional.empty();
                }
            }

            usernameToRoleMap.put(username, role.build());
        }
        return Optional.of(usernameToRoleMap);
    }
}
