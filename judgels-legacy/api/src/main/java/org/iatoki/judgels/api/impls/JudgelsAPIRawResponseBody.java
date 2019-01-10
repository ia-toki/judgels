package org.iatoki.judgels.api.impls;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.iatoki.judgels.api.JudgelsAPIClientException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public final class JudgelsAPIRawResponseBody {

    private final CloseableHttpClient httpClient;
    private final HttpRequestBase httpRequest;
    private final InputStream responseBody;

    public JudgelsAPIRawResponseBody(CloseableHttpClient httpClient, HttpRequestBase httpRequest, InputStream responseBody) {
        this.httpClient = httpClient;
        this.httpRequest = httpRequest;
        this.responseBody = responseBody;
    }

    public String asString() {
        try {
            String string = IOUtils.toString(responseBody);

            responseBody.close();
            httpClient.close();

            return string;
        } catch (IOException e) {
            throw new JudgelsAPIClientException(httpRequest, e);
        }
    }

    public InputStream asRawInputStream() {
        return responseBody;
    }

    public <T> T asObjectFromJson(Type type) {
        try {
            T object =  new Gson().fromJson(asString(), type);

            responseBody.close();
            httpClient.close();

            return object;
        } catch (IOException | JsonSyntaxException e) {
            throw new JudgelsAPIClientException(httpRequest, e);
        }
    }
}
