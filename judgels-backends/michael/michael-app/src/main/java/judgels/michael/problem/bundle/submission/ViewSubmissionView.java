package judgels.michael.problem.bundle.submission;

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.submission.bundle.BundleAnswer;
import judgels.sandalphon.api.submission.bundle.BundleSubmission;
import judgels.sandalphon.api.submission.bundle.ItemGradingResult;

public class ViewSubmissionView extends TemplateView {
    private final BundleSubmission submission;
    private final BundleAnswer answer;
    private final Profile profile;

    public ViewSubmissionView(
            HtmlTemplate template,
            BundleSubmission submission,
            BundleAnswer answer,
            Profile profile) {

        super("viewSubmissionView.ftl", template);
        this.submission = submission;
        this.answer = answer;
        this.profile = profile;
    }

    public BundleSubmission getSubmission() {
        return submission;
    }

    public Map<String, ItemGradingResult> getGradingResults() {
        Map<String, ItemGradingResult> results = submission.getLatestGrading().getDetails();

        Map<Integer, String> numberToJidMap = new TreeMap<>();
        for (Map.Entry<String, ItemGradingResult> entry : results.entrySet()) {
            numberToJidMap.put(entry.getValue().getNumber(), entry.getKey());
        }

        Map<String, ItemGradingResult> sortedResults = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : numberToJidMap.entrySet()) {
            String jid = numberToJidMap.get(entry.getKey());
            sortedResults.put(jid, results.get(jid));
        }

        return ImmutableMap.copyOf(sortedResults);
    }

    public BundleAnswer getAnswer() {
        return answer;
    }

    public Profile getProfile() {
        return profile;
    }
}
