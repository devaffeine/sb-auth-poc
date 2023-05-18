package com.devaffeine.whatsup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SessionManager {
    public record Coord(int gatewayId, int clientId) {

    }

    private final static Map<Long, String> users = new ConcurrentHashMap<>();

    private final static Map<String, Long> sessions = new ConcurrentHashMap<>();

    public void createSession(int clientId, int gatewayId, String phone) {
        long sessionId = createSessionId(clientId, gatewayId);
        users.put(sessionId, phone);
        sessions.put(phone, sessionId);
    }

    public void disconnect(int clientId, int gatewayId) {
        var user = users.remove(createSessionId(clientId, gatewayId));
        if(user != null) {
            sessions.remove(user);
        }
    }

    public String getUser(int clientId, int gatewayId) {
        return users.get(createSessionId(clientId, gatewayId));
    }

    public Coord getSession(String phone) {
        long sessionId = sessions.get(phone);
        return new Coord((int)((sessionId >>> 32)), (int)(sessionId) );
    }

    private long createSessionId(int clientId, int gatewayId) {
        long result = gatewayId;
        result <<= 32;
        result |= clientId;
        return result;
    }
}
