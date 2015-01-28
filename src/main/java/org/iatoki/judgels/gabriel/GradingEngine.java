package org.iatoki.judgels.gabriel;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class GradingEngine {

    private final ThreadPoolExecutor threadPoolExecutor;
    private final FakeSealtiel sealtiel;

    public GradingEngine() {
        int threadPool = (Runtime.getRuntime().availableProcessors() - 1) * 1 * 2;
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPool);

        this.sealtiel = new FakeSealtiel(new File("/Users/fushar/grading-responses"), new File("/Users/fushar/grading-requests"));

        System.out.println("Starting Gabriel using "+threadPool+" threads.");
    }

    public void run() throws InterruptedException {
        while (true) {
            waitUntilAvailable();

            FakeClientMessage message = sealtiel.fetchMessage();
            processMessage(message);

            Thread.sleep(200);
        }
    }

    private void waitUntilAvailable() throws InterruptedException{
        while (threadPoolExecutor.getActiveCount() == threadPoolExecutor.getMaximumPoolSize()) {
            Thread.sleep(50);
        }
    }

    private void processMessage(FakeClientMessage message) {
        if (message == null) {
            return;
        }

        try {
            GradingRequest request = GradingRequests.parseFromJson(message.getMessageType(), message.getMessage());
            GradingWorker runner = GradingWorkers.newWorker("sourcechannel", request, sealtiel);
            runner.run();

            //threadPoolExecutor.submit(runner);
        } catch (BadGradingRequestException e) {
            System.out.println(e.getMessage());
        }
    }
}
