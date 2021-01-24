package org.iatoki.judgels.sandalphon.role;

import org.iatoki.judgels.jophiel.JophielSessionUtils;
import play.mvc.Http;

public class RoleChecker {
    public boolean isAdmin(Http.Request req) {
        return JophielSessionUtils.hasRole(req, "ADMIN");
    }

    public boolean isWriter(Http.Request req) {
        return !JophielSessionUtils.hasRole(req, "COACH");
    }
}
