package com.devaffeine.whatsup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Gateway {
    private AtomicInteger ai = new AtomicInteger();
    private int id;
    private Map<Integer, Client> clients = new HashMap<>();
    private Map<Client, Integer> clientIds = new HashMap<>();
    private SessionManager sessions;
    private MessageService messages;

    private DelivererService deliverer;

    private AtomicInteger offlineDeliverCount = new AtomicInteger();

    public Gateway(int id, SessionManager sessions, MessageService messages) {
        this.id = id;
        this.sessions = sessions;
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public void connect(Client client) {
        var clientId = ai.incrementAndGet();
        clients.put(clientId, client);
        clientIds.put(client, clientId);
    }

    public void auth(Client client, String phone) {
        var clientId = clientIds.get(client);
        sessions.createSession(clientId, id, phone);
        List<Message> msgs = deliverer.getUndeliveredMsgs(phone);
        deliverOfflineMessages(client, msgs);
        System.out.println("user authenticated " + phone);
    }

    public void disconnect(Client client) {
        var clientId = clientIds.remove(client);
        if(clientId != null) {
            clients.remove(clientId);
            sessions.disconnect(clientId, id);
            System.out.println("user disconnected " + client.getPhone());
        }
    }

    public Message sendMessage(Client client, String dest, String message) {
        var clientId = clientIds.get(client);
        var user = sessions.getUser(clientId, id);
        return this.messages.sendMessage(user, dest, message);
    }

    public DelivererService getDeliverer() {
        return deliverer;
    }

    public void setDeliverer(DelivererService deliverer) {
        this.deliverer = deliverer;
    }

    public void deliverMessage(int clientId, Message message) throws Exception {
        var client = clients.get(clientId);
        if (client != null) {
            client.receiveMessage(message);
        }
        throw new Exception("Client disconnected");
    }

    public void deliverOfflineMessages(Client client, List<Message> messages) {
        System.out.println("delivering " + messages.size() + " offline messages to " + client.getPhone());
        for(var msg : messages) {
            client.receiveMessage(msg);
        }
        offlineDeliverCount.addAndGet(messages.size());
    }

    public int getOfflineDeliverCount() {
        return offlineDeliverCount.get();
    }
}
