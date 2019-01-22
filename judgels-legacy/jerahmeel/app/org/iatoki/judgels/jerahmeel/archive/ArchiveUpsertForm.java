package org.iatoki.judgels.jerahmeel.archive;

import play.data.validation.Constraints;

public final class ArchiveUpsertForm {

    public String parentJid;

    @Constraints.Required
    public String name;

    public String description;

    public String getParentJid() {
        return parentJid;
    }

    public void setParentJid(String parentJid) {
        this.parentJid = parentJid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
