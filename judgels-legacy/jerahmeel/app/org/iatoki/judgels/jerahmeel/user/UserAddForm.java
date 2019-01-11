package org.iatoki.judgels.jerahmeel.user;

import play.data.validation.Constraints;

import java.util.Arrays;
import java.util.List;

public final class UserAddForm {

    @Constraints.Required
    public String username;

    @Constraints.Required
    public String roles;

    public List<String> getRolesAsList() {
        return Arrays.asList(this.roles.split(","));
    }
}
