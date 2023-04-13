package judgels.jophiel.legacy.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.user.User;
import judgels.jophiel.user.UserStore;

@Path("/api/v2/users")
public class LegacyUserResource {
    private final UserStore userStore;
    private final ObjectMapper mapper;

    @Inject
    public LegacyUserResource(UserStore userStore, ObjectMapper mapper) {
        this.userStore = userStore;
        this.mapper = mapper;
    }

    @GET
    @Path("/autocomplete")
    @UnitOfWork(readOnly = true)
    public Response autocompleteUsers(@QueryParam("term") String term, @QueryParam("callback") String callback)
            throws Exception {

        List<User> users = userStore.getUsersByTerm(term);
        List<LegacyAutoComplete> res = Lists.transform(users, user -> new LegacyAutoComplete.Builder()
                .id(user.getJid())
                .label(user.getUsername())
                .value(user.getUsername())
                .build());
        String resJson = mapper.writeValueAsString(res);

        return Response.ok(callback + "(" + resJson + ");", "application/javascript").build();
    }
}
