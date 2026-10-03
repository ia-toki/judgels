package judgels.service.feign;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.StringDecoder;
import feign.optionals.OptionalDecoder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class JudgelsDecoder implements Decoder {
    private final Decoder jacksonDecoder;

    public JudgelsDecoder(Decoder jacksonDecoder) {
        this.jacksonDecoder = new OptionalDecoder(jacksonDecoder);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        for (Map.Entry<String, Collection<String>> entries : response.headers().entrySet()) {
            if (entries.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)) {
                for (String val : entries.getValue()) {
                    if (val.startsWith(MediaType.TEXT_PLAIN)) {
                        return new StringDecoder().decode(response, type);
                    }
                }
            }
        }
        return jacksonDecoder.decode(response, type);
    }
}
