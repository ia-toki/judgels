package org.iatoki.judgels.play.google.analytics;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.RealtimeData;
import org.iatoki.judgels.play.general.GeneralConfig;
import org.iatoki.judgels.play.google.serviceaccount.GoogleServiceAccountConfig;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Singleton
public final class GoogleAnalytics {

    private final Analytics analytics;

    @Inject
    public GoogleAnalytics(GeneralConfig generalConfig, GoogleServiceAccountConfig googleServiceAccountConfig) {
        Credential credential = GoogleServiceAuth.createGoogleServiceAuthCredentials(googleServiceAccountConfig.getClientId(), googleServiceAccountConfig.getClientEmail(), googleServiceAccountConfig.getPrivateKeyId(), googleServiceAccountConfig.getPrivateKey());
        String applicationName = generalConfig.getName() + "/" + generalConfig.getVersion();

        try {
            this.analytics = new Analytics.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential)
                    .setApplicationName(applicationName)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getRealtimeTotalUsers(String viewId) {
        try {
            Analytics.Data.Realtime.Get realtimeRequest = analytics.data().realtime().get("ga:" + viewId, "rt:activeUsers");

            RealtimeData realtimeData = realtimeRequest.execute();

            return Long.parseLong(realtimeData.getTotalsForAllResults().getOrDefault("rt:activeUsers", "0"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
