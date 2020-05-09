package org.iatoki.judgels.sandalphon.controllers.securities;

import org.iatoki.judgels.sandalphon.SandalphonUtils;
import play.mvc.Http;
import play.mvc.Security;

public class HasRole extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context context) {
        return SandalphonUtils.getRolesFromSession();
    }
}
