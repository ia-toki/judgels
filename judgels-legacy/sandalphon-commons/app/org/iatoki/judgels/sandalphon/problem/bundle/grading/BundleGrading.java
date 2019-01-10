package org.iatoki.judgels.sandalphon.problem.bundle.grading;

public final class BundleGrading {

    private final long id;
    private final String jid;
    private final double score;
    private final String details;

    public BundleGrading(long id, String jid, double score, String details) {
        this.id = id;
        this.jid = jid;
        this.score = score;
        this.details = details;
    }

    public long getId() {
        return id;
    }

    public String getJid() {
        return jid;
    }

    public double getScore() {
        return score;
    }

    public String getDetails() {
        return details;
    }
}
