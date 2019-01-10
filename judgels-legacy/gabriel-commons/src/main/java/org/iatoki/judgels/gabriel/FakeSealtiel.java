package org.iatoki.judgels.gabriel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public final class FakeSealtiel {
    private final File sendMedium;
    private final File receiveMedium;

    public FakeSealtiel(File sendMedium, File receiveMedium) {
        this.sendMedium = sendMedium;
        this.receiveMedium = receiveMedium;
    }

    public FakeClientMessage fetchMessage() {
        try {
            String messageAsJson = FileUtils.readFileToString(receiveMedium);
            FileUtils.writeStringToFile(receiveMedium, "");
            return new Gson().fromJson(messageAsJson, FakeClientMessage.class);
        } catch (IOException | JsonSyntaxException e) {
            return null;
        }
    }

    public void sendMessage(FakeClientMessage message) {
        try {
            FileUtils.writeStringToFile(sendMedium, new Gson().toJson(message));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write to medium");
        }
    }
}
