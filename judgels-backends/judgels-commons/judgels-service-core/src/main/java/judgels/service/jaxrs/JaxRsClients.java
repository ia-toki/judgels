package judgels.service.jaxrs;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;

public class JaxRsClients {
    private JaxRsClients() {}

    public static <T> T create(Class<T> serviceClass, String uri) {
        return Feign.builder()
                .contract(new JudgelsContract())
                .encoder(new JudgelsEncoder(new JacksonEncoder(JudgelsObjectMappers.OBJECT_MAPPER)))
                .decoder(new JudgelsDecoder(new JacksonDecoder(JudgelsObjectMappers.OBJECT_MAPPER)))
                .errorDecoder(new JudgelsServiceErrorDecoder())
                .client(new OkHttpClient())
                .target(serviceClass, uri);
    }
}
