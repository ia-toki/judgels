package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import com.google.gson.Gson;
import java.util.Map;

public final class BundleGradingResult {

    private final double score;
    private final Map<String, BundleDetailResult> details;

    public BundleGradingResult(double score, Map<String, BundleDetailResult> details) {
        this.score = score;
        this.details = details;
    }

    public double getScore() {
        return score;
    }

    public Map<String, BundleDetailResult> getDetails() {
        return details;
    }

    public String getDetailsAsJson() {
        return new Gson().toJson(details);
    }
}
