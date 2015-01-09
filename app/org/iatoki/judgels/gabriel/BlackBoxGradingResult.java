package org.iatoki.judgels.gabriel;

public final class BlackBoxGradingResult {
    private final BlackBoxVerdict verdict;
    private final double score;
    private String sampleDetails;
    private String fullDetails;

    public BlackBoxGradingResult(BlackBoxVerdict verdict, double score, String sampleDetails, String fullDetails) {
        this.verdict = verdict;
        this.score = score;
        this.sampleDetails = sampleDetails;
        this.fullDetails = fullDetails;
    }

    public BlackBoxVerdict getVerdict() {
        return verdict;
    }

    public double getScore() {
        return score;
    }

    public String getSampleDetails() {
        return sampleDetails;
    }

    public String getFullDetails() {
        return fullDetails;
    }
}
