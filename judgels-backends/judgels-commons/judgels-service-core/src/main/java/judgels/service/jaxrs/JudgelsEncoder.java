package judgels.service.jaxrs;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class JudgelsEncoder implements Encoder {
    private final Encoder jacksonEncoder;

    public JudgelsEncoder(Encoder jaxRsEncoder) {
        this.jacksonEncoder = jaxRsEncoder;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        for (Map.Entry<String, Collection<String>> entries : template.headers().entrySet()) {
            if (entries.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)) {
                for (String val : entries.getValue()) {
                    if (val.startsWith(MediaType.TEXT_PLAIN)) {
                        new Encoder.Default().encode(object, bodyType, template);
                        return;
                    }
                }
            }
        }
        jacksonEncoder.encode(object, bodyType, template);
    }
}
