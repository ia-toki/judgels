package judgels.service.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.palantir.conjure.java.api.config.service.UserAgent;
import com.palantir.conjure.java.client.config.ClientConfiguration;
import com.palantir.conjure.java.client.config.ClientConfigurations;
import com.palantir.conjure.java.client.jaxrs.FeignJaxRsClientBuilder;
import com.palantir.conjure.java.okhttp.HostEventsSink;
import com.palantir.conjure.java.okhttp.HostMetricsRegistry;
import com.palantir.conjure.java.serialization.ObjectMappers;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
        ObjectMapper objectMapper = JudgelsObjectMappers.configure(ObjectMappers.newClientObjectMapper());
        HostEventsSink eventsSink = new HostMetricsRegistry();

        // This is an unfortunate hack to inject a custom object mapper to the JAX-RS client.
        try {
            Constructor<FeignJaxRsClientBuilder> ctor =
                    FeignJaxRsClientBuilder.class.getDeclaredConstructor(ClientConfiguration.class);
            ctor.setAccessible(true);
            FeignJaxRsClientBuilder builder = ctor.newInstance(clientConfig);

            Field builderMapper = FeignJaxRsClientBuilder.class.getDeclaredField("JSON_OBJECT_MAPPER");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(builderMapper, builderMapper.getModifiers() & ~Modifier.FINAL);
            builderMapper.setAccessible(true);
            builderMapper.set(builder, objectMapper);

            return ((FeignJaxRsClientBuilder) (builder.hostEventsSink(eventsSink))).build(serviceClass, userAgent);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
