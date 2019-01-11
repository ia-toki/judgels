package org.iatoki.judgels.jerahmeel.problemset;

public class ProblemSetWithScore {

    private final ProblemSet problemSet;
    private final double score;

    public ProblemSetWithScore(ProblemSet problemSet, double score) {
        this.problemSet = problemSet;
        this.score = score;
    }

    public ProblemSet getProblemSet() {
        return problemSet;
    }

    public double getScore() {
        return score;
    }
}
