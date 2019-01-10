package org.iatoki.judgels.sandalphon.client.lesson;

import play.data.validation.Constraints;

public final class ClientLessonUpsertForm {

    @Constraints.Required
    public String clientJid;
}
