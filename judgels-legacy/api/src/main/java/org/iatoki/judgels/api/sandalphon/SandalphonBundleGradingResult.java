package org.iatoki.judgels.api.sandalphon;

import com.google.gson.Gson;

import java.util.Map;

public final class SandalphonBundleGradingResult {

    private final double score;
    private final Map<String, SandalphonBundleDetailResult> details;

    public SandalphonBundleGradingResult(double score, Map<String, SandalphonBundleDetailResult> details) {
        this.score = score;
        this.details = details;
    }

    public double getScore() {
        return score;
    }

    public Map<String, SandalphonBundleDetailResult> getDetails() {
        return details;
    }

    public String getDetailsAsJson() {
        return new Gson().toJson(details);
    }
}
