package org.iatoki.judgels.sandalphon.client.problem;

import play.data.validation.Constraints;

public final class ClientProblemUpsertForm {

    @Constraints.Required
    public String clientJid;
}
