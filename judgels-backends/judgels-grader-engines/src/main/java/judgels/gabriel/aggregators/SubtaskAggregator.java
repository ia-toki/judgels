package judgels.gabriel.aggregators;

import java.util.List;
import judgels.gabriel.api.SubtaskVerdict;
import judgels.gabriel.api.Verdict;

public class SubtaskAggregator {
    public SubtaskVerdict aggregate(List<SubtaskVerdict> subtaskVerdicts) {
        Verdict aggregatedVerdict = Verdict.ACCEPTED;
        double aggregatedPoints = 0;

        for (SubtaskVerdict subtaskVerdict : subtaskVerdicts) {
            Verdict verdict = subtaskVerdict.getVerdict();
            if (verdict.ordinal() > aggregatedVerdict.ordinal()) {
                aggregatedVerdict = verdict;
            }

            aggregatedPoints += subtaskVerdict.getPoints();
        }

        // This case can only logically happen for subtasks in an output-only problem,
        // where the SKIPPED verdicts are due to unsubmitted output files.
        if (aggregatedVerdict == Verdict.SKIPPED) {
            aggregatedVerdict = Verdict.OK;
        }

        return SubtaskVerdict.of(aggregatedVerdict, aggregatedPoints);
    }
}
