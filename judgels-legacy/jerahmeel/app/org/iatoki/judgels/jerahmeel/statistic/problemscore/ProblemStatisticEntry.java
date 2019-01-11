package org.iatoki.judgels.jerahmeel.statistic.problemscore;

public final class ProblemStatisticEntry implements Comparable<ProblemStatisticEntry> {

    private final String problemJid;
    private final long totalSubmissions;

    public ProblemStatisticEntry(String problemJid, long totalSubmissions) {
        this.problemJid = problemJid;
        this.totalSubmissions = totalSubmissions;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public long getTotalSubmissions() {
        return totalSubmissions;
    }

    @Override
    public int compareTo(ProblemStatisticEntry o) {
        return Long.compare(o.getTotalSubmissions(), this.getTotalSubmissions());
    }
}
