package com.devaffeine.whatsup;

public class MessageStorage {
    private PersistentDB db;

    private PubSub pubSub;

    public MessageStorage(PubSub pubSub, PersistentDB db) {
        this.db = db;
        this.pubSub = pubSub;
    }

    public void saveMessage(Message message) {
        db.put("messages", message.id(), message);
    }
}
