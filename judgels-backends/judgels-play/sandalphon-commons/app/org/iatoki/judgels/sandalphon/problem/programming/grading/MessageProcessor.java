package org.iatoki.judgels.sandalphon.problem.programming.grading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.palantir.conjure.java.api.errors.RemoteException;
import judgels.gabriel.api.GradingResponse;
import judgels.sealtiel.api.message.Message;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import play.db.jpa.JPAApi;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class MessageProcessor implements Runnable {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModules(new Jdk8Module(), new GuavaModule());

    private final JPAApi jpaApi;
    private final ProgrammingSubmissionService submissionService;
    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;
    private final Message message;

    public MessageProcessor(JPAApi jpaApi, ProgrammingSubmissionService submissionService, BasicAuthHeader sealtielClientAuthHeader, MessageService messageService, Message message) {
        this.jpaApi = jpaApi;
        this.submissionService = submissionService;
        this.sealtielClientAuthHeader = sealtielClientAuthHeader;
        this.messageService = messageService;
        this.message = message;
    }

    @Override
    public void run() {
        jpaApi.withTransaction(() -> {
                try {
                    GradingResponse response = MAPPER.readValue(message.getContent(), GradingResponse.class);

                    boolean gradingExists = false;

                    // temporary solution
                    // problem is: grading response arrives before the grading model persistance has been flushed

                    for (int i = 0; i < 3; i++) {
                        if (submissionService.gradingExists(response.getGradingJid())) {
                            gradingExists = true;
                            break;
                        }

                        try {
                            Thread.sleep(TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    if (gradingExists) {
                        submissionService.grade(response.getGradingJid(), response.getResult(), message.getSourceJid(), "localhost");
                    } else {
                        System.out.println("Grading JID " + response.getGradingJid() + " not found!");
                    }
                    messageService.confirmMessage(sealtielClientAuthHeader, message.getId());
                } catch (RemoteException | IOException e) {
                    System.out.println("Bad grading response!");
                    e.printStackTrace();
                }
            });
    }
}
