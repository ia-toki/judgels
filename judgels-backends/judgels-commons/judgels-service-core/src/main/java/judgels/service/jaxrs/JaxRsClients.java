package judgels.service.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.palantir.conjure.java.api.config.service.UserAgent;
import com.palantir.conjure.java.client.config.ClientConfiguration;
import com.palantir.conjure.java.client.config.ClientConfigurations;
import com.palantir.conjure.java.client.jaxrs.feignimpl.Java8OptionalAwareContract;
import com.palantir.conjure.java.client.jaxrs.feignimpl.PathTemplateHeaderEnrichmentContract;
import com.palantir.conjure.java.client.jaxrs.feignimpl.PathTemplateHeaderRewriter;
import com.palantir.conjure.java.client.jaxrs.feignimpl.SlashEncodingContract;
import com.palantir.conjure.java.okhttp.HostEventsSink;
import com.palantir.conjure.java.okhttp.HostMetricsRegistry;
import com.palantir.conjure.java.okhttp.OkHttpClients;
import com.palantir.conjure.java.serialization.ObjectMappers;
import feign.Contract;
import feign.EmptyContainerDecoder;
import feign.Feign;
import feign.InputStreamDelegateDecoder;
import feign.InputStreamDelegateEncoder;
import feign.Java8OptionalAwareDecoder;
import feign.Logger;
import feign.NeverReturnNullDecoder;
import feign.Request;
import feign.Retryer;
import feign.TextDelegateDecoder;
import feign.TextDelegateEncoder;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import feign.okhttp.OkHttpClient;
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
import judgels.service.jersey.JudgelsObjectMappers;

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

    // Adapted from com.palantir.conjure.java.client.jaxrs.AbstractFeignJaxRsClientBuilder.
    // This exists because we want to modify the object mapper via JudgelsObjectMappers.
    public static <T> T create(Class<T> serviceClass, String uri, UserAgent userAgent) {
        List<String> uris = ImmutableList.of(uri);
        ClientConfiguration clientConfig = ClientConfigurations.of(uris, defaultSslSocketFactory, defaultTrustManager);
        ObjectMapper objectMapper = JudgelsObjectMappers.configure(ObjectMappers.newClientObjectMapper());
        HostEventsSink eventsSink = new HostMetricsRegistry();

        return Feign.builder()
                .contract(createContract())
                .encoder(createEncoder(objectMapper))
                .decoder(createDecoder(objectMapper))
                .requestInterceptor(PathTemplateHeaderRewriter.INSTANCE)
                .client(new OkHttpClient(OkHttpClients.create(clientConfig, userAgent, eventsSink, serviceClass)))
                .options(createRequestOptions(clientConfig))
                .logLevel(Logger.Level.NONE)  // we use OkHttp interceptors for logging. (note that NONE is the default)
                .retryer(new Retryer.Default(0, 0, 1))  // use OkHttp retry mechanism only
                .target(serviceClass, uri);
    }

    private static Contract createContract() {
        return new PathTemplateHeaderEnrichmentContract(
                new SlashEncodingContract(
                        new Java8OptionalAwareContract(
                                new JAXRSContract())));
    }

    private static Request.Options createRequestOptions(ClientConfiguration clientConfig) {
        return new Request.Options(
                Math.toIntExact(clientConfig.connectTimeout().toMillis()),
                Math.toIntExact(clientConfig.readTimeout().toMillis()));
    }

    private static Encoder createEncoder(ObjectMapper objectMapper) {
        return new InputStreamDelegateEncoder(
                new TextDelegateEncoder(
                        new JacksonEncoder(objectMapper)));
    }

    private static Decoder createDecoder(ObjectMapper objectMapper) {
        return new NeverReturnNullDecoder(
                new Java8OptionalAwareDecoder(
                        new EmptyContainerDecoder(
                                objectMapper,
                                new InputStreamDelegateDecoder(
                                        new TextDelegateDecoder(
                                                new JacksonDecoder(objectMapper))))));
    }
}
