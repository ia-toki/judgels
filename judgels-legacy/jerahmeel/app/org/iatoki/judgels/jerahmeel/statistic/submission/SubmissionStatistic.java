package org.iatoki.judgels.jerahmeel.statistic.submission;

public final class SubmissionStatistic {

    private final long thisHour;
    private final long thisDay;
    private final long thisWeek;
    private final long thisMonth;
    private final long thisYear;
    private final long overLastHour;
    private final long overLastDay;
    private final long overLastWeek;
    private final long overLastMonth;
    private final long overLastYear;
    private final long overall;

    public SubmissionStatistic(long thisHour, long thisDay, long thisWeek, long thisMonth, long thisYear, long overLastHour, long overLastDay, long overLastWeek, long overLastMonth, long overLastYear, long overall) {
        this.thisHour = thisHour;
        this.thisDay = thisDay;
        this.thisWeek = thisWeek;
        this.thisMonth = thisMonth;
        this.thisYear = thisYear;
        this.overLastHour = overLastHour;
        this.overLastDay = overLastDay;
        this.overLastWeek = overLastWeek;
        this.overLastMonth = overLastMonth;
        this.overLastYear = overLastYear;
        this.overall = overall;
    }

    public long getThisHour() {
        return thisHour;
    }

    public long getThisDay() {
        return thisDay;
    }

    public long getThisWeek() {
        return thisWeek;
    }

    public long getThisMonth() {
        return thisMonth;
    }

    public long getThisYear() {
        return thisYear;
    }

    public long getOverLastHour() {
        return overLastHour;
    }

    public long getOverLastDay() {
        return overLastDay;
    }

    public long getOverLastWeek() {
        return overLastWeek;
    }

    public long getOverLastMonth() {
        return overLastMonth;
    }

    public long getOverLastYear() {
        return overLastYear;
    }

    public long getOverall() {
        return overall;
    }
}
