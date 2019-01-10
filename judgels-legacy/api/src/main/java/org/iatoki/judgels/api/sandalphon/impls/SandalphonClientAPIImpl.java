package org.iatoki.judgels.api.sandalphon.impls;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.iatoki.judgels.api.impls.AbstractJudgelsClientAPIImpl;
import org.iatoki.judgels.api.sandalphon.SandalphonBundleAnswer;
import org.iatoki.judgels.api.sandalphon.SandalphonBundleGradingResult;
import org.iatoki.judgels.api.sandalphon.SandalphonBundleProblemStatementRenderRequestParam;
import org.iatoki.judgels.api.sandalphon.SandalphonClientAPI;
import org.iatoki.judgels.api.sandalphon.SandalphonLesson;
import org.iatoki.judgels.api.sandalphon.SandalphonLessonStatementRenderRequestParam;
import org.iatoki.judgels.api.sandalphon.SandalphonProblem;
import org.iatoki.judgels.api.sandalphon.SandalphonProgrammingProblemStatementRenderRequestParam;
import org.iatoki.judgels.api.sandalphon.SandalphonProgrammingProblemInfo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public final class SandalphonClientAPIImpl extends AbstractJudgelsClientAPIImpl implements SandalphonClientAPI {

    private static final String TOTP_ENCRYPTION_ALGORITHM = "HmacSHA1";

    public SandalphonClientAPIImpl(String baseUrl, String clientJid, String clientSecret) {
        super(baseUrl, clientJid, clientSecret);
    }

    @Override
    public SandalphonProblem findClientProblem(String problemJid, String problemSecret) {
        JsonObject body = new JsonObject();

        body.addProperty("clientJid", getClientJid());
        body.addProperty("problemJid", problemJid);
        body.addProperty("problemSecret", problemSecret);

        return sendPostRequest("/problems/client", body).asObjectFromJson(SandalphonProblem.class);
    }

    @Override
    public String getProgrammingProblemStatementRenderAPIEndpoint(String problemJid) {
        return getEndpoint(interpolatePath("/problems/programming/:problemJid/statements", problemJid));
    }

    @Override
    public String getBundleProblemStatementRenderAPIEndpoint(String problemJid) {
        return getEndpoint(interpolatePath("/problems/bundle/:problemJid/statements", problemJid));
    }

    @Override
    public String constructProgrammingProblemStatementRenderAPIRequestBody(String problemJid, SandalphonProgrammingProblemStatementRenderRequestParam param) {
        List<NameValuePair> params = ImmutableList.of(
                new BasicNameValuePair("problemJid", problemJid),
                new BasicNameValuePair("clientJid", getClientJid()),
                new BasicNameValuePair("totpCode", "" + computeTOTPCode(param.getProblemSecret(), param.getCurrentMillis())),
                new BasicNameValuePair("statementLanguage", param.getStatementLanguage()),
                new BasicNameValuePair("switchStatementLanguageUrl", param.getSwitchStatementLanguageUrl()),
                new BasicNameValuePair("postSubmitUrl", param.getPostSubmitUrl()),
                new BasicNameValuePair("reasonNotAllowedToSubmit", param.getReasonNotAllowedToSubmit()),
                new BasicNameValuePair("allowedGradingLanguages", param.getAllowedGradingLanguages())
        );

        return URLEncodedUtils.format(params, "UTF-8");
    }

    @Override
    public String constructBundleProblemStatementRenderAPIRequestBody(String problemJid, SandalphonBundleProblemStatementRenderRequestParam param) {
        List<NameValuePair> params = ImmutableList.of(
                new BasicNameValuePair("problemJid", problemJid),
                new BasicNameValuePair("clientJid", getClientJid()),
                new BasicNameValuePair("totpCode", "" + computeTOTPCode(param.getProblemSecret(), param.getCurrentMillis())),
                new BasicNameValuePair("statementLanguage", param.getStatementLanguage()),
                new BasicNameValuePair("switchStatementLanguageUrl", param.getSwitchStatementLanguageUrl()),
                new BasicNameValuePair("postSubmitUrl", param.getPostSubmitUrl()),
                new BasicNameValuePair("reasonNotAllowedToSubmit", param.getReasonNotAllowedToSubmit())
        );

        return URLEncodedUtils.format(params, "UTF-8");
    }

    @Override
    public String getProblemStatementMediaRenderAPIEndpoint(String problemJid, String mediaFilename) {
        return getEndpoint(interpolatePath("/problems/:problemJid/statements/media/:mediaFilename", problemJid, mediaFilename));
    }

    @Override
    public SandalphonLesson findClientLesson(String lessonJid, String lessonSecret) {
        JsonObject body = new JsonObject();

        body.addProperty("clientJid", getClientJid());
        body.addProperty("lessonJid", lessonJid);
        body.addProperty("lessonSecret", lessonSecret);

        return sendPostRequest("/lessons/client", body).asObjectFromJson(SandalphonLesson.class);
    }

    @Override
    public String getLessonStatementRenderAPIEndpoint(String lessonJid) {
        return getEndpoint(interpolatePath("/lessons/:lessonJid/statements", lessonJid));
    }

    @Override
    public String getLessonStatementMediaRenderAPIEndpoint(String lessonJid, String mediaFilename) {
        return getEndpoint(interpolatePath("/lessons/:lessonJid/statements/media/:mediaFilename", lessonJid, mediaFilename));
    }

    @Override
    public String constructLessonStatementRenderAPIRequestBody(String lessonJid, SandalphonLessonStatementRenderRequestParam param) {
        List<NameValuePair> params = ImmutableList.of(
                new BasicNameValuePair("lessonJid", lessonJid), new BasicNameValuePair("clientJid", getClientJid()),
                new BasicNameValuePair("totpCode", "" + computeTOTPCode(param.getLessonSecret(), param.getCurrentMillis())),
                new BasicNameValuePair("statementLanguage", param.getStatementLanguage()),
                new BasicNameValuePair("switchStatementLanguageUrl", param.getSwitchStatementLanguageUrl())
        );

        return URLEncodedUtils.format(params, "UTF-8");
    }

    @Override
    public SandalphonProgrammingProblemInfo getProgrammingProblemInfo(String problemJid) {
        return sendGetRequest(interpolatePath("/problems/programming/:problemJid", problemJid)).asObjectFromJson(SandalphonProgrammingProblemInfo.class);
    }

    @Override
    public InputStream downloadProgrammingProblemGradingFiles(String problemJid) {
        return sendGetRequest(interpolatePath("/problems/programming/:problemJid/grading", problemJid)).asRawInputStream();
    }

    @Override
    public SandalphonBundleGradingResult gradeBundleProblem(String problemJid, SandalphonBundleAnswer answer) {
        JsonElement requestBody = new Gson().toJsonTree(answer);
        return sendPostRequest(interpolatePath("/problems/bundle/:problemJid/grade", problemJid), requestBody).asObjectFromJson(SandalphonBundleGradingResult.class);
    }

    // source: https://github.com/wstrange/GoogleAuth/blob/master/src/main/java/com/warrenstrange/googleauth/GoogleAuthenticator.java

    /*
     * Copyright (c) 2014-2015 Enrico M. Crisostomo
     * All rights reserved.
     *
     * Modified and used by Jordan Fernando <fernandojordan.92@gmail.com>
     *
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions are met:
     *
     *   * Redistributions of source code must retain the above copyright notice, this
     *     list of conditions and the following disclaimer.
     *
     *   * Redistributions in binary form must reproduce the above copyright notice,
     *     this list of conditions and the following disclaimer in the documentation
     *     and/or other materials provided with the distribution.
     *
     *   * Neither the name of the author nor the names of its
     *     contributors may be used to endorse or promote products derived from
     *     this software without specific prior written permission.
     *
     * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
     * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
     * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
     * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
     * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
     * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
     * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
     * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
     * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
     * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */
    private int computeTOTPCode(String keyString, long timeMillis) {
        long totpMod = 1000000;
        long timeStep = 30000;

        byte[] key = keyString.getBytes();
        byte[] data = new byte[8];
        long value = timeMillis / timeStep;

        for (int signKey = 8; signKey-- > 0; value >>>= 8) {
            data[signKey] = (byte) ((int) value);
        }

        SecretKeySpec var15 = new SecretKeySpec(key, TOTP_ENCRYPTION_ALGORITHM);

        try {
            Mac ex = Mac.getInstance(TOTP_ENCRYPTION_ALGORITHM);
            ex.init(var15);
            byte[] hash = ex.doFinal(data);
            int offset = hash[hash.length - 1] & 15;
            long truncatedHash = 0L;

            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (long) (hash[offset + i] & 255);
            }

            truncatedHash &= 2147483647L;
            truncatedHash %= totpMod;
            return (int) truncatedHash;
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("The operation cannot be performed now.", e);
        }
    }
}
