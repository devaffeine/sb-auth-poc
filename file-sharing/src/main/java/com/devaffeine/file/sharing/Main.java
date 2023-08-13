package com.devaffeine.file.sharing;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        var server = new FileSharingServer(Path.of("data", "servers", "server1"));
        var client = new FileSharingClient("client1", Path.of("data", "clients", "client1", "device1"), server, scheduler);
        scheduler.awaitTermination(1, TimeUnit.DAYS);
    }
}