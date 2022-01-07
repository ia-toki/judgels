package judgels.service.jaxrs;

import feign.MethodMetadata;
import feign.Param;
import feign.jaxrs.JAXRSContract;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;

public class JudgelsContract extends JAXRSContract {
    @Override
    protected MethodMetadata parseAndValidateMetadata(Class<?> targetType, Method method) {
        MethodMetadata metadata = super.parseAndValidateMetadata(targetType, method);

        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];

            if (clazz.equals(Optional.class)) {
                if (containsClass(annotations[i], HeaderParam.class)) {
                    metadata.indexToExpanderClass().put(i, HeaderParamOptionalExpander.class);
                } else if (containsClass(annotations[i], QueryParam.class)) {
                    metadata.indexToExpanderClass().put(i, QueryParamOptionalExpander.class);
                }
            }
        }
        return metadata;
    }

    private static boolean containsClass(Annotation[] annotations, Class<?> clazz) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    public static class HeaderParamOptionalExpander implements Param.Expander {
        @Override
        public String expand(Object value) {
            Optional<?> optional = (Optional<?>) value;
            return optional.isPresent() ? Objects.toString(optional.get()) : "";
        }
    }

    public static class QueryParamOptionalExpander implements Param.Expander {
        @Override
        public String expand(Object value) {
            Optional<?> optional = (Optional<?>) value;
            return optional.isPresent() ? Objects.toString(optional.get()) : null;
        }
    }
}
