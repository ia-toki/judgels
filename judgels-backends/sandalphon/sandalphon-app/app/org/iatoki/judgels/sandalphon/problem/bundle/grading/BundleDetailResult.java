package org.iatoki.judgels.sandalphon.problem.bundle.grading;

public final class BundleDetailResult {

    private final int number;
    private final double score;

    public BundleDetailResult(int number, double score) {
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
