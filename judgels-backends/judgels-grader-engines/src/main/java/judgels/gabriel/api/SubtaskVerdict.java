package judgels.gabriel.api;

import org.immutables.value.Value;

@Value.Immutable
public interface SubtaskVerdict {
    Verdict getVerdict();
    double getPoints();

    static SubtaskVerdict of(Verdict verdict, double points) {
        return new SubtaskVerdict.Builder().verdict(verdict).points(points).build();
    }

    class Builder extends ImmutableSubtaskVerdict.Builder {}
}
