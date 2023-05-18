package com.devaffeine.whatsup;

public class Client {
    private String phone;
    private Gateway gateway;

    public Client(String phone, Gateway gateway) {
        this.gateway = gateway;
        this.phone = phone;
        this.gateway.connect(this);
    }

    public void auth() {
        this.gateway.auth(this, phone);
    }

    public void disconnect() {
        this.gateway.disconnect(this);
    }
}
