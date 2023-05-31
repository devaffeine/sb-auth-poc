package com.devaffeine.whatsup;

import com.devaffeine.whatsup.tools.PersistentDB;
import com.devaffeine.whatsup.tools.PubSub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelivererService {
    private PubSub pubSub;

    private SessionManager sessionManager;

    private Map<Integer, Gateway> gatewaysMap;

    private PersistentDB messagesDb;

    public DelivererService(PubSub pubSub, List<Gateway> gateways, SessionManager sessionManager, PersistentDB messagesDb) {
        this.pubSub = pubSub;
        this.gatewaysMap = new HashMap<>();
        gateways.forEach(x -> gatewaysMap.put(x.getId(), x));
        this.pubSub.subscribe("messages", 0, this::onMessage);
        this.sessionManager = sessionManager;
        this.messagesDb = messagesDb;
    }

    public boolean onMessage(List<Object> messages) {
        try {
            for (Object msg : messages) {
                deliverMessage((Message) msg);
            }
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void deliverMessage(Message msg) {
        try {
            var toUser = sessionManager.getSession(msg.to());
            if (toUser == null) {
                throw new Exception("user is not connected");
            }
            var gateway = gatewaysMap.get(toUser.gatewayId());
            gateway.deliverMessage(toUser.clientId(), msg);
        }
        catch (Exception ex) {
            messagesDb.put("undelivered", msg.to() + "-" + msg.id(), msg);
        }
    }

    public List<Message> getUndeliveredMsgs(String user) {
        var submap = messagesDb.query("undelivered", user + "-", user + ".");
        var list = submap.values().stream().map(x -> (Message)x).toList();
        submap.keySet().forEach(x -> messagesDb.remove("undelivered", x));
        return list;
    }
}
