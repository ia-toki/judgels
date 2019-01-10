package org.iatoki.judgels.play;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.RealtimeData;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @deprecated Has been restructured to different package.
 */
@Deprecated
public final class GoogleAnalytics {

    private static GoogleAnalytics INSTANCE;
    private Analytics analytics;

    private GoogleAnalytics(Analytics analytics) {
        this.analytics = analytics;
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

    public static synchronized void buildInstance(Credential credential, String applicationName) {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("GoogleAnalytics instance has already been built");
        }
        try {
            INSTANCE = new GoogleAnalytics(new Analytics.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(applicationName).build());
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GoogleAnalytics getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("GoogleAnalytics instance has not been built");
        }
        return INSTANCE;
    }
}
