package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleDetailResult;

import java.util.Map;

public final class BundleSubmissionUtils {

    private BundleSubmissionUtils() {
        // prevent instantiation
    }

    public static Map<String, BundleDetailResult> parseGradingResult(BundleSubmission submission) {
        Map<String, BundleDetailResult> gradingResult = new Gson().fromJson(submission.getLatestDetails(), new TypeToken<Map<String, BundleDetailResult>>() { }.getType());

        Map<Long, String> numberToJidMap = Maps.newTreeMap();
        for (Map.Entry<String, BundleDetailResult> entry : gradingResult.entrySet()) {
            numberToJidMap.put(entry.getValue().getNumber(), entry.getKey());
        }

        ImmutableMap.Builder<String, BundleDetailResult> sortedGradingResult = ImmutableMap.builder();
        for (Map.Entry<Long, String> entry : numberToJidMap.entrySet()) {
            String currentJid = numberToJidMap.get(entry.getKey());
            sortedGradingResult.put(currentJid, gradingResult.get(currentJid));
        }

        return sortedGradingResult.build();
    }
}
