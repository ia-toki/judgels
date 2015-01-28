package org.iatoki.judgels.gabriel.blackbox;

public final class SubtaskResult {
    private final NormalVerdict verdict;
    private final double score;

    public SubtaskResult(NormalVerdict verdict, double score) {
        this.verdict = verdict;
        this.score = score;
    }

    public NormalVerdict getVerdict() {
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

        SubtaskResult that = (SubtaskResult) o;

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
