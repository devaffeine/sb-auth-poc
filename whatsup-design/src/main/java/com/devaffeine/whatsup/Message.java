package com.devaffeine.whatsup;

import java.util.UUID;

public record Message(UUID id, String from, String to, String content) {
}
