package org.iatoki.judgels.sandalphon.client;

import play.data.validation.Constraints;

public final class ClientUpsertForm {

    @Constraints.Required
    public String name;
}
