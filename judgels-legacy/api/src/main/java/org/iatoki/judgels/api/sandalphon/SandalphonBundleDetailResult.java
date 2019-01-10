package org.iatoki.judgels.api.sandalphon;

public final class SandalphonBundleDetailResult {

    private final long number;
    private final double score;

    public SandalphonBundleDetailResult(long number, double score) {
        this.number = number;
        this.score = score;
    }

    public long getNumber() {
        return number;
    }

    public double getScore() {
        return score;
    }
}
