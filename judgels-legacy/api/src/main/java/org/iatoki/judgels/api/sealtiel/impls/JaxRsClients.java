package org.iatoki.judgels.api.sealtiel.impls;

import com.google.common.collect.ImmutableList;
import com.palantir.remoting3.clients.ClientConfiguration;
import com.palantir.remoting3.clients.ClientConfigurations;
import com.palantir.remoting3.clients.UserAgent;
import com.palantir.remoting3.jaxrs.JaxRsClient;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class JaxRsClients {
    private static X509TrustManager defaultTrustManager;
    private static SSLSocketFactory defaultSslSocketFactory;

    static {
        try {
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init((KeyStore) null);

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }

            defaultTrustManager = (X509TrustManager) trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] {defaultTrustManager}, null);

            defaultSslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new IllegalStateException(e);
        }
    }

    private JaxRsClients() {}

    public static <T> T create(Class<T> serviceClass, String uri, UserAgent userAgent) {
        List<String> uris = ImmutableList.of(uri);
        ClientConfiguration clientConfig = ClientConfigurations.of(uris, defaultSslSocketFactory, defaultTrustManager);
        return JaxRsClient.create(serviceClass, userAgent, clientConfig);
    }
}
