package org.iatoki.judgels.gabriel;

import com.google.gson.JsonSyntaxException;
import org.iatoki.judgels.sealtiel.ClientMessage;
import org.iatoki.judgels.sealtiel.Sealtiel;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class Grader {

    private final int threads;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final Sealtiel sealtiel;

    public Grader(int threads) {
        this.threads = threads;
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
        this.sealtiel = new Sealtiel(GabrielProperties.getInstance().getSealtielClientJid(), GabrielProperties.getInstance().getSealtielClientSecret(), GabrielProperties.getInstance().getSealtielBaseUrl());
    }

    public void run() throws InterruptedException {
        GabrielLogger.getLogger().info("Gabriel started; using " + threads + " threads.");

        while (true) {
            waitUntilAvailable();

            try {
                ClientMessage message = sealtiel.fetchMessage();
                if (message != null) {
                    processMessage(message);
                }
            } catch (JsonSyntaxException | IOException e) {
                GabrielLogger.getLogger().error("Bad grading request", e);
                if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                    GabrielLogger.getLogger().error("Message:", e.getMessage());
                }
            }

            Thread.sleep(200);
        }
    }

    private void waitUntilAvailable() throws InterruptedException{
        while (threadPoolExecutor.getActiveCount() == threadPoolExecutor.getMaximumPoolSize()) {
            Thread.sleep(50);
        }
    }

    private void processMessage(ClientMessage message) {
        try {
            GradingRequest request = GradingRequests.parseFromJson(message.getMessageType(), message.getMessage());
            GabrielLogger.getLogger().info("New grading request: {}", request.getGradingJid());

            GradingWorker worker = GradingWorkers.newWorker(message.getSourceClientJid(), request, sealtiel, message.getId());

            threadPoolExecutor.submit(worker);
        } catch (BadGradingRequestException | JsonSyntaxException e) {
            GabrielLogger.getLogger().error("Bad grading request", e);
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                GabrielLogger.getLogger().error("Message:", e.getMessage());
            }
        }
    }
}
