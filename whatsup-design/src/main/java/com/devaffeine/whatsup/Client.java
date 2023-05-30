package com.devaffeine.whatsup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private String phone;
    private Gateway gateway;
    private Map<String, List<Message>> conversation;
    private Map<String, AtomicInteger> received;
    private Map<String, AtomicInteger> sent;

    public Client(String phone, Gateway gateway) {
        this.gateway = gateway;
        this.phone = phone;
        this.gateway.connect(this);
        this.conversation = new HashMap<>();
        this.received = new HashMap<>();
        this.sent = new HashMap<>();
    }

    public String getPhone() {
        return phone;
    }

    public void auth() {
        this.gateway.auth(this, phone);
    }

    public void sendMessage(String to, String content) {
        var message = gateway.sendMessage(this, to, content);
        conversation.computeIfAbsent(to, x -> new ArrayList<>()).add(message);
        sent.computeIfAbsent(to, x -> new AtomicInteger(0)).addAndGet(1);
    }

    public void receiveMessage(Message message) {
        System.out.println("message received " + message.toString() + ", received at " + LocalDateTime.now());
        conversation.computeIfAbsent(message.from(), x -> new ArrayList<>()).add(message);
        received.computeIfAbsent(message.from(), x -> new AtomicInteger(0)).addAndGet(1);
    }

    public void disconnect() {
        this.gateway.disconnect(this);
    }

    public List<String> getIteractions() {
        return new ArrayList<>(conversation.keySet());
    }

    public int getReceived(String client) {
        if(received.containsKey(client)) {
            return received.get(client).get();
        }
        return 0;
    }

    public int getSent(String client) {
        if(sent.containsKey(client)) {
            return sent.get(client).get();
        }
        return 0;
    }
}
