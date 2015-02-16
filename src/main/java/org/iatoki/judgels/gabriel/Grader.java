package org.iatoki.judgels.gabriel;

import org.iatoki.judgels.sealtiel.client.ClientMessage;
import org.iatoki.judgels.sealtiel.client.Sealtiel;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class Grader {

    private final ThreadPoolExecutor threadPoolExecutor;
    private final Sealtiel sealtiel;

    public Grader() {
        int threadPool = (Runtime.getRuntime().availableProcessors() - 1) * 1 * 2;
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPool);

        if (GabrielProperties.getInstance().getSealtielFakeSendMedium() != null) {
            File sendMedium = new File(GabrielProperties.getInstance().getSealtielFakeSendMedium());
            File receiveMedium = new File(GabrielProperties.getInstance().getSealtielFakeReceiveMedium());

            this.sealtiel = new Sealtiel(GabrielProperties.getInstance().getSealtielClientJid(), GabrielProperties.getInstance().getSealtielClientSecret(), GabrielProperties.getInstance().getSealtielBaseUrl());
        } else {
            throw new RuntimeException("No sealtiel info found");
        }

        System.out.println("Starting Gabriel using "+threadPool+" threads.");
    }

    public void run() throws InterruptedException {
        while (true) {
            waitUntilAvailable();

            try {
                ClientMessage message = sealtiel.fetchMessage();
                processMessage(message);
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
        if (message == null) {
            return;
        }

        try {
            GradingRequest request = GradingRequests.parseFromJson(message.getMessageType(), message.getMessage());
            GradingWorker worker = GradingWorkers.newWorker("sourcechannel", request, sealtiel);

            threadPoolExecutor.submit(worker);
        } catch (BadGradingRequestException e) {
            System.out.println(e.getMessage());
        }
    }
}
