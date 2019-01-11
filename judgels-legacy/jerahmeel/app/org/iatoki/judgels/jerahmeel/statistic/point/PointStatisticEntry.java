package org.iatoki.judgels.jerahmeel.statistic.point;

public final class PointStatisticEntry implements Comparable<PointStatisticEntry> {

    private final String userJid;
    private final double totalPoints;
    private final long totalProblems;

    public PointStatisticEntry(String userJid, double totalPoints, long totalProblems) {
        this.userJid = userJid;
        this.totalPoints = totalPoints;
        this.totalProblems = totalProblems;
    }

    public String getUserJid() {
        return userJid;
    }

    public double getTotalPoints() {
        return totalPoints;
    }

    public long getTotalProblems() {
        return totalProblems;
    }

    @Override
    public int compareTo(PointStatisticEntry o) {
        if (Double.compare(o.getTotalPoints(), this.getTotalPoints()) == 0) {
            return Long.compare(o.getTotalProblems(), this.getTotalProblems());
        }

        return Double.compare(o.getTotalPoints(), this.getTotalPoints());
    }
}
