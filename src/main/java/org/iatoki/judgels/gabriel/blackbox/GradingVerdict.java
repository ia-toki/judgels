package org.iatoki.judgels.gabriel.blackbox;

public final class GradingVerdict {
    private final OverallVerdict overallVerdict;
    private final double overallScore;
    private final GradingVerdictDetails details;

    public GradingVerdict(OverallVerdict overallVerdict, double overallScore, GradingVerdictDetails details) {
        this.overallVerdict = overallVerdict;
        this.overallScore = overallScore;
        this.details = details;
    }

    public OverallVerdict getOverallVerdict() {
        return overallVerdict;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public GradingVerdictDetails getDetails() {
        return details;
    }
}
