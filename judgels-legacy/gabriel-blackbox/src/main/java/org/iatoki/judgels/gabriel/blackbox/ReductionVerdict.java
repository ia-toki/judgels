package org.iatoki.judgels.gabriel.blackbox;

public final class ReductionVerdict implements NormalVerdict {
    private final NormalVerdict worstVerdict;

    private ReductionVerdict(NormalVerdict worstVerdict) {
        this.worstVerdict = worstVerdict;
    }

    public static ReductionVerdict okWithWorstVerdict(NormalVerdict worstVerdict) {
        return new ReductionVerdict(worstVerdict);
    }

    @Override
    public String getCode() {
        return ScoringVerdict.OK.getCode();
    }

    @Override
    public String getDescription() {
        return ScoringVerdict.OK.getCode() + " (worst: " + worstVerdict.getCode() + ")";
    }
}
