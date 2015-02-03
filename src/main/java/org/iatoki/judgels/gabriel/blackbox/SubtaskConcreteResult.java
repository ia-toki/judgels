package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Verdict;

public final class SubtaskConcreteResult {
    private final Verdict verdict;
    private final double score;

    public SubtaskConcreteResult(Verdict verdict, double score) {
        this.verdict = verdict;
        this.score = score;
    }

    public SubtaskConcreteResult(SubtaskResult result) {
        this.verdict = new Verdict(result.getVerdict().getCode(), result.getVerdict().getName());
        this.score = result.getScore();
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

        SubtaskConcreteResult that = (SubtaskConcreteResult) o;

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
