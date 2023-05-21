package com.devaffeine.whatsup;

import java.util.UUID;

public class MessageService {

    private PubSub pubSub;

    public MessageService(PubSub pubSub) {
        this.pubSub = pubSub;
    }

    public void sendMessage(String from, String to, String message) {
        this.pubSub.publish("messages", new Message(UUID.randomUUID(), from, to, message));
    }
}
