package com.devaffeine.whatsup;

public class SessionManager {
    public record Coord(int gatewayId, int clientId) {

    }

    private MemoryDB memoryDB;

    public SessionManager(MemoryDB memoryDB) {
        this.memoryDB = memoryDB;
    }

    public void createSession(int clientId, int gatewayId, String phone) {
        long sessionId = createSessionId(clientId, gatewayId);
        memoryDB.set("session-" + sessionId, phone);
        memoryDB.set("user-" + phone, sessionId);
    }

    public void disconnect(int clientId, int gatewayId) {
        var user = memoryDB.remove("session-" + createSessionId(clientId, gatewayId));
        if(user != null) {
            memoryDB.remove("user-" + user);
        }
    }

    public String getUser(int clientId, int gatewayId) {
        return (String) memoryDB.get("session-" + createSessionId(clientId, gatewayId));
    }

    public Coord getSession(String phone) {
        long sessionId = (long)memoryDB.get("user-" + phone);
        return new Coord((int)((sessionId >>> 32)), (int)(sessionId) );
    }

    private long createSessionId(int clientId, int gatewayId) {
        long result = gatewayId;
        result <<= 32;
        result |= clientId;
        return result;
    }
}
