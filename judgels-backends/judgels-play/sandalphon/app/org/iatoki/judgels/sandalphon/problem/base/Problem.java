package org.iatoki.judgels.sandalphon.problem.base;

import java.util.Date;

public final class Problem {

    private final long id;
    private final String jid;
    private final String slug;
    private final String authorJid;
    private final String additionalNote;
    private final Date lastUpdateTime;

    private final ProblemType type;

    public Problem(long id, String jid, String slug, String authorJid, String additionalNote, Date lastUpdateTime, ProblemType type) {
        this.id = id;
        this.jid = jid;
        this.slug = slug;
        this.authorJid = authorJid;
        this.additionalNote = additionalNote;
        this.lastUpdateTime = lastUpdateTime;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getJid() {
        return jid;
    }

    public String getSlug() {
        return slug;
    }

    public String getAuthorJid() {
        return authorJid;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public ProblemType getType() {
        return type;
    }
}
