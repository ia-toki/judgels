package org.iatoki.judgels.sandalphon.problem.programming.grading;

import com.google.gson.Gson;
import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.sealtiel.SealtielClientAPI;
import org.iatoki.judgels.api.sealtiel.SealtielMessage;
import org.iatoki.judgels.gabriel.GradingResponse;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import play.db.jpa.JPA;

import java.util.concurrent.TimeUnit;

public final class MessageProcessor implements Runnable {

    private final ProgrammingSubmissionService submissionService;
    private final SealtielClientAPI sealtielClientAPI;
    private final SealtielMessage message;

    public MessageProcessor(ProgrammingSubmissionService submissionService, SealtielClientAPI sealtielClientAPI, SealtielMessage message) {
        this.submissionService = submissionService;
        this.sealtielClientAPI = sealtielClientAPI;
        this.message = message;
    }

    @Override
    public void run() {
        JPA.withTransaction(() -> {
                try {
                    GradingResponse response = new Gson().fromJson(message.getMessage(), GradingResponse.class);

                    boolean gradingExists = false;

                    // temporary solution
                    // problem is: grading response arrives before the grading model persistance has been flushed

                    for (int i = 0; i < 3; i++) {
                        if (submissionService.gradingExists(response.getGradingJid())) {
                            gradingExists = true;
                            break;
                        }

                        Thread.sleep(TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS));
                    }

                    if (gradingExists) {
                        submissionService.grade(response.getGradingJid(), response.getResult(), message.getSourceClientJid(), "localhost");
                    } else {
                        System.out.println("Grading JID " + response.getGradingJid() + " not found!");
                    }
                    sealtielClientAPI.acknowledgeMessage(message.getId());
                } catch (JudgelsAPIClientException e) {
                    System.out.println("Bad grading response!");
                    e.printStackTrace();
                }
            });
    }
}
