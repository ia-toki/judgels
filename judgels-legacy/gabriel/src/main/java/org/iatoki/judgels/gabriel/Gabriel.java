package org.iatoki.judgels.gabriel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.sandalphon.SandalphonClientAPI;
import org.iatoki.judgels.api.sandalphon.SandalphonFactory;
import org.iatoki.judgels.api.sealtiel.SealtielClientAPI;
import org.iatoki.judgels.api.sealtiel.SealtielFactory;
import org.iatoki.judgels.api.sealtiel.SealtielMessage;

import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class Gabriel {

    private final int threads;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final SealtielClientAPI sealtielClientAPI;
    private final SandalphonClientAPI sandalphonClientAPI;

    public Gabriel(int threads) {
        this.threads = threads;
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);

        String sealtielBaseUrl = GabrielProperties.getInstance().getSealtielBaseUrl();
        String sealtielClientJid = GabrielProperties.getInstance().getSealtielClientJid();
        String sealtielClientSecret = GabrielProperties.getInstance().getSealtielClientSecret();

        this.sealtielClientAPI = SealtielFactory.createSealtiel(sealtielBaseUrl).connectToClientAPI(sealtielClientJid, sealtielClientSecret);

        String sandalphonBaseUrl = GabrielProperties.getInstance().getSandalphonBaseUrl();
        String sandalphonClientJid = GabrielProperties.getInstance().getSandalphonClientJid();
        String sandalphonClientSecret = GabrielProperties.getInstance().getSandalphonClientSecret();

        this.sandalphonClientAPI = SandalphonFactory.createSandalphon(sandalphonBaseUrl).connectToClientAPI(sandalphonClientJid, sandalphonClientSecret);
    }

    public void run() throws InterruptedException {
        GabrielLogger.getLogger().info("Gabriel started; using " + threads + " threads.");

        while (true) {
            waitUntilAvailable();

            SealtielMessage message = null;

            try {
                message = sealtielClientAPI.fetchMessage();
            } catch (JudgelsAPIClientException e) {
                GabrielLogger.getLogger().error("Bad grading request", e);
                if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                    GabrielLogger.getLogger().error("Message:", e.getMessage());
                }
            }
            if (message != null) {
                processMessage(message);
            }

            Thread.sleep(200);
        }
    }

    private void waitUntilAvailable() throws InterruptedException {
        while (threadPoolExecutor.getActiveCount() == threadPoolExecutor.getMaximumPoolSize()) {
            Thread.sleep(50);
        }
    }

    private void processMessage(SealtielMessage message) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(byte[].class, (JsonSerializer<byte[]>) (src, typeOfSrc, context) -> new JsonPrimitive(Base64.getEncoder().encodeToString(src)));
            builder.registerTypeAdapter(byte[].class, (JsonDeserializer<byte[]>) (json, typeOfT, context) -> Base64.getDecoder().decode(json.getAsString()));
            Gson gson = builder.create();

            GradingRequest request;
            try {
                request = gson.fromJson(message.getMessage(), GradingRequest.class);
            } catch (Exception e) {
                request = new Gson().fromJson(message.getMessage(), GradingRequest.class);
            }

            GabrielLogger.getLogger().info("New grading request: {}", request.getGradingJid());

            GabrielWorker worker = new GabrielWorker(message.getSourceClientJid(), request, sealtielClientAPI, sandalphonClientAPI, message.getId());

            threadPoolExecutor.submit(worker);
        } catch (JsonSyntaxException e) {
            GabrielLogger.getLogger().error("Bad grading request", e);
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                GabrielLogger.getLogger().error("Message:", e.getMessage());
            }
        }
    }
}
