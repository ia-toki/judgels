package org.iatoki.judgels.sandalphon.user;

import play.data.validation.Constraints;

import java.util.Arrays;
import java.util.List;

public final class UserEditForm {

    @Constraints.Required
    public String roles;

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public List<String> getRolesAsList() {
        return Arrays.asList(this.roles.split(","));
    }
}
