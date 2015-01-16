package org.iatoki.judgels.gabriel;

import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingRequest;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingRunner;
import org.iatoki.judgels.sealtiel.client.ClientMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class Grader {

    private final ThreadPoolExecutor threadPoolExecutor;
    private final FakeSealtiel sealtiel;
    private long runnerId;

    private Grader(String fakeMessage) {
        int threadPool = (Runtime.getRuntime().availableProcessors() - 1) * 1 * 2;

        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPool);
        this.sealtiel = new FakeSealtiel(fakeMessage);
        this.runnerId = 0;

        System.out.println("Starting Gabriel using "+threadPool+" threads.");
    }

    public static void main(String[] args) {
        Grader grader = new Grader(args[0]);

        try {
            grader.run();
        } catch (InterruptedException e) {
            // nothing
        }
    }

    public void run() throws InterruptedException {
        while (true) {

            while (threadPoolExecutor.getActiveCount() == threadPoolExecutor.getMaximumPoolSize()) {
                Thread.sleep(50);
            }

            ClientMessage message = sealtiel.fetchMessage();

            if (message != null) {
                processMessage(message);
            }

            Thread.sleep(200);
        }
    }

    private void processMessage(ClientMessage message) {

        try {
            Class<?> requestClass = Class.forName("org.iatoki.judgels.gabriel.blackbox." + message.getMessageType());
            GradingRequest request = new Gson().<BlackBoxGradingRequest>fromJson(message.getMessage(), requestClass);

            if (request instanceof BlackBoxGradingRequest) {
                threadPoolExecutor.submit(new BlackBoxGradingRunner(++runnerId, sealtiel, (BlackBoxGradingRequest) request));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Malformed grading request: message id " + message.getId());
        }
    }
}
