package com.devaffeine.whatsup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private String phone;
    private Gateway gateway;
    private Map<String, List<String>> conversation;

    public Client(String phone, Gateway gateway) {
        this.gateway = gateway;
        this.phone = phone;
        this.gateway.connect(this);
        this.conversation = new HashMap<>();
    }

    public String getPhone() {
        return phone;
    }

    public void auth() {
        this.gateway.auth(this, phone);
    }

    public void sendMessage(String to, String message) {
        conversation.computeIfAbsent(to, x -> new ArrayList<>()).add(message);
        gateway.sendMessage(this, to, message);
    }

    public void receiveMessage(String from, String message) {
        System.out.println("message received from: " + from + ", body: " + message);
        conversation.computeIfAbsent(from, x -> new ArrayList<>()).add(message);
    }

    public void disconnect() {
        this.gateway.disconnect(this);
    }
}
