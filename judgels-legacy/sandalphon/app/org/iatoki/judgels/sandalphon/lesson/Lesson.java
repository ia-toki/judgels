package org.iatoki.judgels.sandalphon.lesson;

import java.util.Date;

public final class Lesson {

    private final long id;
    private final String jid;
    private final String slug;
    private final String authorJid;
    private final String additionalNote;
    private final Date lastUpdateTime;

    public Lesson(long id, String jid, String slug, String authorJid, String additionalNote, Date lastUpdateTime) {
        this.id = id;
        this.jid = jid;
        this.slug = slug;
        this.authorJid = authorJid;
        this.additionalNote = additionalNote;
        this.lastUpdateTime = lastUpdateTime;
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
}
