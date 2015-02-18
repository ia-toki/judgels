package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Verdict;

public final class SubtaskFinalResult {
    private final int id;
    private final Verdict verdict;
    private final double score;

    public SubtaskFinalResult(int id, Verdict verdict, double score) {
        this.id = id;
        this.verdict = verdict;
        this.score = score;
    }

    public SubtaskFinalResult(int id, SubtaskResult result) {
        this.id = id;
        this.verdict = new Verdict(result.getVerdict().getCode(), result.getVerdict().getDescription());
        this.score = result.getScore();
    }

    public int getId() {
        return id;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public double getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SubtaskFinalResult that = (SubtaskFinalResult) o;

        if (Double.compare(that.score, score) != 0) {
            return false;
        }
        if (!verdict.equals(that.verdict)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = verdict.hashCode();
        temp = Double.doubleToLongBits(score);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
