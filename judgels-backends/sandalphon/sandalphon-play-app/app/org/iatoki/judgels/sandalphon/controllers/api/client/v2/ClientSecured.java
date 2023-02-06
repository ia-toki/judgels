package org.iatoki.judgels.sandalphon.controllers.api.client.v2;

import java.util.Optional;
import javax.inject.Inject;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.client.ClientChecker;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class ClientSecured extends Security.Authenticator {
    private final ClientChecker clientChecker;

    @Inject
    public ClientSecured(ClientChecker clientChecker) {
        this.clientChecker = clientChecker;
    }

    @Override
    public Optional<String> getUsername(Http.Request req) {
        Optional<String> authHeaderString = req.getHeaders().get("Authorization");
        if (!authHeaderString.isPresent()) {
            return Optional.empty();
        }
        BasicAuthHeader authHeader = BasicAuthHeader.valueOf(authHeaderString.get());
        return Optional.of(clientChecker.check(authHeader).getJid());
    }

    @Override
    public Result onUnauthorized(Http.Request req) {
        return unauthorized();
    }
}
