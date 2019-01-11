package org.iatoki.judgels.jerahmeel.statistic.problemscore;

public final class ProblemScoreStatisticEntry implements Comparable<ProblemScoreStatisticEntry> {

    private final String userJid;
    private final double score;
    private final long time;

    public ProblemScoreStatisticEntry(String userJid, double score, long time) {
        this.userJid = userJid;
        this.score = score;
        this.time = time;
    }

    public String getUserJid() {
        return userJid;
    }

    public double getScore() {
        return score;
    }

    public long getTime() {
        return time;
    }

    @Override
    public int compareTo(ProblemScoreStatisticEntry o) {
        if (o.getScore() == this.getScore()) {
            return Long.compare(this.getTime(), o.getTime());
        }
        return Double.compare(o.getScore(), this.getScore());
    }
}
