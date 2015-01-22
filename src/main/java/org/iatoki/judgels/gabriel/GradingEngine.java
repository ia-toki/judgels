package org.iatoki.judgels.gabriel;

import com.google.gson.Gson;
import org.iatoki.judgels.sealtiel.client.ClientMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class GradingEngine {

    private final ThreadPoolExecutor threadPoolExecutor;
    private final FakeSealtiel sealtiel;
    private final GradingHandler handler;

    public GradingEngine(String fakeMessage) {
        int threadPool = (Runtime.getRuntime().availableProcessors() - 1) * 1 * 2;
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPool);

        this.sealtiel = new FakeSealtiel(fakeMessage);

        this.handler = new GradingHandler() {
            @Override
            public void onComplete(String senderChannel, String submissionJid, GradingResult result) {
                GradingResponse response = new GradingResponse(submissionJid, result);

                ClientMessage message = new ClientMessage(senderChannel, "GradingResponse", new Gson().toJson(response));
                sealtiel.sendMessage(message);
            }
        };

        System.out.println("Starting Gabriel using "+threadPool+" threads.");
    }

    public void run() throws InterruptedException {
        while (true) {
            waitUntilAvailable();

            ClientMessage message = sealtiel.fetchMessage();
            processMessage(message);

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
            GradingRequest request = GradingRequests.newRequestFromJson(message.getMessageType(), message.getMessage());
            GradingRunner runner = GradingRunners.newRunner(message.getSourceClientChannel(), request, handler);

            threadPoolExecutor.submit(runner);
        } catch (BadGradingRequestException e) {
            System.out.println(e.getMessage());
        }
    }
}
