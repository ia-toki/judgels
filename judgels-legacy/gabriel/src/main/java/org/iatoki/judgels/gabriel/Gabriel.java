package org.iatoki.judgels.gabriel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palantir.conjure.java.api.config.service.UserAgent;
import com.palantir.conjure.java.api.errors.RemoteException;
import judgels.gabriel.api.GabrielObjectMapper;
import judgels.gabriel.api.GradingRequest;
import judgels.sealtiel.api.message.Message;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;
import org.iatoki.judgels.api.sandalphon.SandalphonClientAPI;
import org.iatoki.judgels.api.sandalphon.SandalphonFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class Gabriel {
    private static final ObjectMapper MAPPER = GabrielObjectMapper.getInstance();

    private final int threads;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;
    private final SandalphonClientAPI sandalphonClientAPI;

    public Gabriel(int threads) {
        this.threads = threads;
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);

        String sealtielBaseUrl = GabrielProperties.getInstance().getSealtielBaseUrl();
        String sealtielClientJid = GabrielProperties.getInstance().getSealtielClientJid();
        String sealtielClientSecret = GabrielProperties.getInstance().getSealtielClientSecret();
        this.sealtielClientAuthHeader = BasicAuthHeader.of(Client.of(sealtielClientJid, sealtielClientSecret));

        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("gabriel", UserAgent.Agent.DEFAULT_VERSION));
        this.messageService = JaxRsClients.create(MessageService.class, sealtielBaseUrl, userAgent);

        String sandalphonBaseUrl = GabrielProperties.getInstance().getSandalphonBaseUrl();
        String sandalphonClientJid = GabrielProperties.getInstance().getSandalphonClientJid();
        String sandalphonClientSecret = GabrielProperties.getInstance().getSandalphonClientSecret();

        this.sandalphonClientAPI = SandalphonFactory.createSandalphon(sandalphonBaseUrl).connectToClientAPI(sandalphonClientJid, sandalphonClientSecret);
    }

    public void run() throws InterruptedException {
        GabrielLogger.getLogger().info("Gabriel started; using " + threads + " threads.");

        while (true) {
            waitUntilAvailable();

            Optional<Message> message = null;

            try {
                message = messageService.receiveMessage(sealtielClientAuthHeader);
            } catch (RemoteException e) {
                GabrielLogger.getLogger().error("Bad grading request", e);
                if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                    GabrielLogger.getLogger().error("Message:", e.getMessage());
                }
            }
            if (message.isPresent()) {
                processMessage(message.get());
            }

            Thread.sleep(200);
        }
    }

    private void waitUntilAvailable() throws InterruptedException {
        while (threadPoolExecutor.getActiveCount() == threadPoolExecutor.getMaximumPoolSize()) {
            Thread.sleep(50);
        }
    }

    private void processMessage(Message message) {
        try {
            GradingRequest request = MAPPER.readValue(message.getContent(), GradingRequest.class);

            GabrielLogger.getLogger().info("New grading request: {}", request.getGradingJid());

            GabrielWorker worker = new GabrielWorker(message.getSourceJid(), request, sealtielClientAuthHeader, messageService, sandalphonClientAPI, message.getId());

            threadPoolExecutor.submit(worker);
        } catch (IOException e) {
            GabrielLogger.getLogger().error("Bad grading request", e);
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                GabrielLogger.getLogger().error("Message:", e.getMessage());
            }
        }
    }
}
