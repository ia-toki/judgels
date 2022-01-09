package judgels.service.jaxrs;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.StringDecoder;
import feign.optionals.OptionalDecoder;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class JudgelsDecoder implements Decoder {
    private final Decoder jacksonDecoder;

    public JudgelsDecoder(Decoder jaxRsDecoder) {
        this.jacksonDecoder = new OptionalDecoder(jaxRsDecoder);
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
