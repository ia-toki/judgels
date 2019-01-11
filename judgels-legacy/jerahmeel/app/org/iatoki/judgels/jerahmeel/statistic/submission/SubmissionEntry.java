package org.iatoki.judgels.jerahmeel.statistic.submission;

public final class SubmissionEntry implements Comparable<SubmissionEntry> {

    private final String authorJid;
    private final String problemJid;
    private final double score;
    private final long time;

    public SubmissionEntry(String authorJid, String problemJid, double score, long time) {
        this.authorJid = authorJid;
        this.problemJid = problemJid;
        this.score = score;
        this.time = time;
    }

    public String getAuthorJid() {
        return authorJid;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public double getScore() {
        return score;
    }

    public long getTime() {
        return time;
    }

    @Override
    public int compareTo(SubmissionEntry o) {
        return Long.compare(o.getTime(), this.getTime());
    }
}
