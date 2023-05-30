package com.devaffeine.whatsup;

import com.devaffeine.whatsup.tools.PersistentDB;
import com.devaffeine.whatsup.tools.PubSub;

import java.util.List;

public class MessageStorage {
    private PersistentDB db;

    private PubSub pubSub;

    public MessageStorage(PubSub pubSub, PersistentDB db) {
        this.db = db;
        this.pubSub = pubSub;
        this.pubSub.subscribe("messages", 0, this::onMessage);
    }

    public boolean onMessage(List<Object> messages) {
        try {
            for (Object msg : messages) {
                saveMessage((Message) msg);
            }
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void saveMessage(Message message) {
        db.put("messages", message.id(), message);
    }
}
