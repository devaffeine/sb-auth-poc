package com.devaffeine.whatsup;

import com.devaffeine.whatsup.tools.PubSub;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageService {

    private PubSub pubSub;

    public MessageService(PubSub pubSub) {
        this.pubSub = pubSub;
    }

    public Message sendMessage(String from, String to, String content) {
        var message = new Message(UUID.randomUUID(), from, to, content, LocalDateTime.now());
        this.pubSub.publish("messages", message);
        return message;
    }
}
