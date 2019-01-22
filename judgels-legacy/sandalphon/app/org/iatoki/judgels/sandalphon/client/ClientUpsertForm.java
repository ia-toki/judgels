package org.iatoki.judgels.sandalphon.client;

import play.data.validation.Constraints;

public final class ClientUpsertForm {

    @Constraints.Required
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
