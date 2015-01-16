package org.iatoki.judgels.gabriel;

import com.google.gson.Gson;
import org.iatoki.judgels.sealtiel.client.ClientMessage;

public final class FakeSealtiel {

    private ClientMessage clientMessage;

    public FakeSealtiel(String fakeMessage) {
        Gson gson = new Gson();
        System.out.println(fakeMessage);
        this.clientMessage = gson.fromJson(fakeMessage, ClientMessage.class);
    }

    public ClientMessage fetchMessage() {
        ClientMessage message = clientMessage;
        clientMessage = null;

        return  message;
    }

    public void sendMessage(ClientMessage message) {
        Gson gson = new Gson();

        System.out.println(gson.toJson(message));
    }
}
