package judgels.sandalphon;

import java.util.Date;

public final class GitCommit {

    private final String hash;
    private final String userJid;
    private final Date time;
    private final String title;
    private final String description;

    public GitCommit(String hash, String userJid, Date time, String title, String description) {
        this.hash = hash;
        this.userJid = userJid;
        this.time = time;
        this.title = title;
        this.description = description;
    }

    public String getHash() {
        return hash;
    }

    public String getUserJid() {
        return userJid;
    }

    public Date getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
