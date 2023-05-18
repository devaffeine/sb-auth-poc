package com.devaffeine.whatsup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Gateway {
    private AtomicInteger ai = new AtomicInteger();
    private int id;
    private Map<Integer, Client> clients = new HashMap<>();
    private Map<Client, Integer> clientIds = new HashMap<>();

    private SessionManager sessions;

    public Gateway(int id, SessionManager sessions) {
        this.id = id;
        this.sessions = sessions;
    }

    public void connect(Client client) {
        var clientId = ai.incrementAndGet();
        clients.put(clientId, client);
        clientIds.put(client, clientId);
    }

    public void auth(Client client, String phone) {
        var clientId = clientIds.get(client);
        sessions.createSession(clientId, id, phone);
    }

    public void disconnect(Client client) {
        var clientId = clientIds.remove(client);
        clients.remove(clientId);
        sessions.disconnect(clientId, id);
    }
}
