package com.devaffeine.whatsup;

import java.time.LocalDateTime;
import java.util.UUID;

public record Message(UUID id, String from, String to, String content, LocalDateTime time) {
    @Override
    public String toString() {
        return String.format("Message from: %s, to: %s, content: %s, at: %s", from, to, content, time.toString());
    }
}
