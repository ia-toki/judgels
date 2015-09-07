package org.iatoki.judgels.gabriel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.sealtiel.SealtielAPI;
import org.iatoki.judgels.api.sealtiel.SealtielFactory;
import org.iatoki.judgels.api.sealtiel.SealtielMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class Gabriel {

    private final int threads;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final SealtielAPI sealtielAPI;

    public Gabriel(int threads) {
        this.threads = threads;
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);

        String sealtielBaseUrl = GabrielProperties.getInstance().getSealtielBaseUrl();
        String sealtielClientJid = GabrielProperties.getInstance().getSealtielClientJid();
        String sealtielClientSecret = GabrielProperties.getInstance().getSealtielClientSecret();

        this.sealtielAPI = SealtielFactory.createSealtiel(sealtielBaseUrl).connectWithBasicAuth(sealtielClientJid, sealtielClientSecret);
    }

    public void run() throws InterruptedException {
        GabrielLogger.getLogger().info("Gabriel started; using " + threads + " threads.");

        while (true) {
            waitUntilAvailable();

            SealtielMessage message = null;

            try {
                message = sealtielAPI.fetchMessage();
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
            GradingRequest request = new Gson().fromJson(message.getMessage(), GradingRequest.class);

            GabrielLogger.getLogger().info("New grading request: {}", request.getGradingJid());

            GabrielWorker worker = new GabrielWorker(message.getSourceClientJid(), request, sealtielAPI, message.getId());

            threadPoolExecutor.submit(worker);
        } catch (JsonSyntaxException e) {
            GabrielLogger.getLogger().error("Bad grading request", e);
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                GabrielLogger.getLogger().error("Message:", e.getMessage());
            }
        }
    }
}
