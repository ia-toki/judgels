package judgels.service.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.Collection;
import java.util.Map;

public class BearerTokenRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        for (Map.Entry<String, Collection<String>> entries : template.headers().entrySet()) {
            if (entries.getKey().equalsIgnoreCase(HttpHeaders.AUTHORIZATION)) {
                for (String val : entries.getValue()) {
                    if (val.equals("Bearer ")) {
                        template.removeHeader(HttpHeaders.AUTHORIZATION);
                    }
                }
            }
        }
    }
}
