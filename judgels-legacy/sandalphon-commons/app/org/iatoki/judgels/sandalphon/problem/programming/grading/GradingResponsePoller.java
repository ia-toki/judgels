package org.iatoki.judgels.sandalphon.problem.programming.grading;

import akka.actor.Scheduler;
import com.palantir.remoting.api.errors.RemoteException;
import judgels.sealtiel.api.message.Message;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class GradingResponsePoller implements Runnable {

    private final Scheduler scheduler;
    private final ExecutionContext executor;
    private final ProgrammingSubmissionService submissionService;
    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;
    private final long interval;

    private boolean isConnected;

    public GradingResponsePoller(Scheduler scheduler, ExecutionContext executor, ProgrammingSubmissionService submissionService, BasicAuthHeader sealtielClientAuthHeader, MessageService messageService, long interval) {
        this.scheduler = scheduler;
        this.executor = executor;
        this.submissionService = submissionService;
        this.sealtielClientAuthHeader = sealtielClientAuthHeader;
        this.messageService = messageService;
        this.interval = interval;
        this.isConnected = false;
    }

    @Override
    public void run() {
        long checkPoint = System.currentTimeMillis();
        Optional<Message> message = Optional.empty();
        do {
            try {
                message = messageService.receiveMessage(sealtielClientAuthHeader);
                if (message.isPresent()) {
                    if (!isConnected) {
                        System.out.println("Connected to Sealtiel!");
                        isConnected = true;
                    }

                    MessageProcessor processor = new MessageProcessor(submissionService, sealtielClientAuthHeader, messageService, message.get());
                    scheduler.scheduleOnce(Duration.create(10, TimeUnit.MILLISECONDS), processor, executor);
                }
            } catch (RemoteException e) {
                if (isConnected) {
                    System.out.println("Disconnected from Sealtiel!");
                    System.out.println(e.getMessage());
                    isConnected = false;
                }
            }
        } while ((System.currentTimeMillis() - checkPoint < interval) && (message != null));
    }
}
