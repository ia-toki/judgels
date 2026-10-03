package judgels.service.feign;

import feign.Feign;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import judgels.JudgelsObjectMappers;

public class FeignClients {
    private FeignClients() {}

    public static <T> T create(Class<T> clientClass, String uri) {
        return Feign.builder()
                .requestInterceptor(new BearerTokenRequestInterceptor())
                .encoder(new FormEncoder(new JudgelsEncoder(new JacksonEncoder(JudgelsObjectMappers.OBJECT_MAPPER))))
                .queryMapEncoder(new JudgelsQueryMapEncoder())
                .decoder(new JudgelsDecoder(new JacksonDecoder(JudgelsObjectMappers.OBJECT_MAPPER)))
                .errorDecoder(new JudgelsServiceErrorDecoder())
                .client(new OkHttpClient())
                .target(clientClass, uri);

    }
}
