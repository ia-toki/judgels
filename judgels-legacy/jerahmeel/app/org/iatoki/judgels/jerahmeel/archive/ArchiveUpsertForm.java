package org.iatoki.judgels.jerahmeel.archive;

import play.data.validation.Constraints;

public final class ArchiveUpsertForm {

    public String parentJid;

    @Constraints.Required
    public String name;

    public String description;
}
