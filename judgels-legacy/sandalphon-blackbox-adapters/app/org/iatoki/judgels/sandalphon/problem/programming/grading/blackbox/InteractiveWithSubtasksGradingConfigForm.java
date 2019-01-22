package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

public final class InteractiveWithSubtasksGradingConfigForm extends SingleSourceFileWithSubtasksBlackBoxGradingConfigForm {

    public String communicator;

    public String getCommunicator() {
        return communicator;
    }

    public void setCommunicator(String communicator) {
        this.communicator = communicator;
    }
}
