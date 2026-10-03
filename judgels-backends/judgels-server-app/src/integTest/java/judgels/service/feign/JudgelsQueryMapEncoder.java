package judgels.service.feign;

import feign.QueryMapEncoder;
import feign.querymap.FieldQueryMapEncoder;
import java.util.Map;

public class JudgelsQueryMapEncoder implements QueryMapEncoder {
    private final QueryMapEncoder encoder = new FieldQueryMapEncoder();

    @Override
    public Map<String, Object> encode(Object object) {
        if (object == null) {
            return Map.of();
        }
        return encoder.encode(object);
    }
}
