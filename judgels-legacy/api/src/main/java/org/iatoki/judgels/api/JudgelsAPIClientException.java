package org.iatoki.judgels.api;

import org.apache.http.client.methods.HttpRequestBase;

public final class JudgelsAPIClientException extends RuntimeException {

    private final HttpRequestBase httpRequest;
    private final int statusCode;
    private final String errorMessage;

    public JudgelsAPIClientException(HttpRequestBase httpRequest, int statusCode, String errorMessage) {
        this.httpRequest = httpRequest;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public JudgelsAPIClientException(HttpRequestBase httpRequest, Throwable cause) {
        this.httpRequest = httpRequest;
        this.statusCode = -1;
        this.errorMessage = cause.getMessage();
    }

    public HttpRequestBase getHttpRequest() {
        return httpRequest;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getMessage() {
        return httpRequest.toString() + " request failed with status code " + statusCode + " and error message \"" + errorMessage + "\".";
    }
}
