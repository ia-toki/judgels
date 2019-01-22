package org.iatoki.judgels.jerahmeel.problemset;

import play.data.validation.Constraints;

public final class ProblemSetUpsertForm {

    @Constraints.Required
    public String archiveJid;

    @Constraints.Required
    public String name;

    public String description;

    public String getArchiveJid() {
        return archiveJid;
    }

    public void setArchiveJid(String archiveJid) {
        this.archiveJid = archiveJid;
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
