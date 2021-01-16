package org.iatoki.judgels.sandalphon.problem.bundle.grading;

public final class BundleDetailResult {

    private final long number;
    private final double score;

    public BundleDetailResult(long number, double score) {
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
