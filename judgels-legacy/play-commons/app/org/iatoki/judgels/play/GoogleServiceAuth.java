package org.iatoki.judgels.play;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @deprecated Has been restructured to different package.
 */
@Deprecated
public final class GoogleServiceAuth {

    private GoogleServiceAuth() {
        // prevent instantiation
    }

    public static Credential createGoogleServiceAuthCredentials(String clientId, String clientEmail, String privateKeyId, String privateKey) {
        try {
            GoogleCredential.Builder credentialBuilder = new GoogleCredential.Builder();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("client_id", clientId);
            jsonObject.addProperty("client_email", clientEmail);
            jsonObject.addProperty("private_key_id", privateKeyId);
            jsonObject.addProperty("private_key", privateKey);
            jsonObject.addProperty("type", "service_account");

            return GoogleCredential.fromStream(new ByteArrayInputStream(jsonObject.toString().getBytes(StandardCharsets.UTF_8))).createScoped(ImmutableList.of(AnalyticsScopes.ANALYTICS_READONLY));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
