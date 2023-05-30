package com.devaffeine.whatsup;

import com.devaffeine.whatsup.tools.PubSub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelivererService {
    private PubSub pubSub;

    private SessionManager sessionManager;

    Map<Integer, Gateway> gatewaysMap;

    public DelivererService(PubSub pubSub, List<Gateway> gateways, SessionManager sessionManager) {
        this.pubSub = pubSub;
        this.gatewaysMap = new HashMap<>();
        gateways.forEach(x -> gatewaysMap.put(x.getId(), x));
        this.pubSub.subscribe("messages", 0, this::onMessage);
        this.sessionManager = sessionManager;
    }

    public boolean onMessage(List<Object> messages) {
        try {
            for (Object msg : messages) {
                deliverMessage((Message)msg);
            }
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void deliverMessage(Message msg) {
        var toUser = sessionManager.getSession(msg.to());
        var gateway = gatewaysMap.get(toUser.gatewayId());
        gateway.deliverMessage(toUser.clientId(), msg);
    }
}
