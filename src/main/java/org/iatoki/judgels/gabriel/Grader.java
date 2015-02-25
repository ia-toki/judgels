package org.iatoki.judgels.gabriel;

import org.iatoki.judgels.sealtiel.client.ClientMessage;
import org.iatoki.judgels.sealtiel.client.Sealtiel;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class Grader {

    private final ThreadPoolExecutor threadPoolExecutor;
    private final Sealtiel sealtiel;

    public Grader() {
        int threadPool = Math.max(1, (Runtime.getRuntime().availableProcessors() - 1) * 1 * 2);
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPool);
        this.sealtiel = new Sealtiel(GabrielProperties.getInstance().getSealtielClientJid(), GabrielProperties.getInstance().getSealtielClientSecret(), GabrielProperties.getInstance().getSealtielBaseUrl());

        GabrielLogger.getLogger().info("Gabriel started; using " + threadPool + " threads.");
    }

    public void run() throws InterruptedException {
        while (true) {
            waitUntilAvailable();

            try {
                ClientMessage message = sealtiel.fetchMessage();
                if (message != null) {
                    processMessage(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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
        } catch (BadGradingRequestException e) {
            GabrielLogger.getLogger().warn("Bad grading request: " + e.getMessage());
        }
    }
}
