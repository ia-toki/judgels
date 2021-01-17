package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import java.util.Date;
import java.util.List;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGrading;

public final class BundleSubmission {

    private final long id;
    private final String jid;
    private final String problemJid;
    private final String containerJid;
    private final String authorJid;
    private final Date time;
    private final String ipAddress;
    private final List<BundleGrading> gradings;

    public BundleSubmission(long id, String jid, String problemJid, String containerJid, String authorJid, Date time, String ipAddress, List<BundleGrading> gradings) {
        this.id = id;
        this.jid = jid;
        this.problemJid = problemJid;
        this.containerJid = containerJid;
        this.authorJid = authorJid;
        this.time = time;
        this.ipAddress = ipAddress;
        this.gradings = gradings;
    }

    public long getId() {
        return id;
    }

    public String getJid() {
        return jid;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public String getContainerJid() {
        return containerJid;
    }

    public String getAuthorJid() {
        return authorJid;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Date getTime() {
        return time;
    }

    public List<BundleGrading> getGradings() {
        return gradings;
    }

    public double getLatestScore() {
        return gradings.get(gradings.size() - 1).getScore();
    }

    public String getLatestDetails() {
        return gradings.get(gradings.size() - 1).getDetails();
    }

    public String getLatestGradingJid() {
        return gradings.get(gradings.size() - 1).getJid();
    }
}
