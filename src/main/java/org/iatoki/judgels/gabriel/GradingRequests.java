package org.iatoki.judgels.gabriel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingRequest;

public final class GradingRequests {
    private GradingRequests() {
        // prevent instantiation
    }

    public static GradingRequest newRequestFromJson(String type, String json) throws BadGradingRequestException {
        if (type.equals("BlackBoxGradingRequest")) {
            try {
                return new Gson().fromJson(json, BlackBoxGradingRequest.class);
            } catch (JsonSyntaxException e) {
                throw new BadGradingRequestException("Malformed BlackBoxGradingRequest JSON");
            }
        } else {
            throw new BadGradingRequestException("Grading request type unknown: " + type);
        }
    }
}
