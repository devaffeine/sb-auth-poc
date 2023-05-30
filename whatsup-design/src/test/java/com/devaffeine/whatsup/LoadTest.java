package com.devaffeine.whatsup;

import com.devaffeine.whatsup.tools.MemoryDB;
import com.devaffeine.whatsup.tools.PersistentDB;
import com.devaffeine.whatsup.tools.PubSub;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoadTest {
    @Test
    public void testBasicChat() throws InterruptedException {
        var scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        Random random = new Random();
        MemoryDB sessionsDb = new MemoryDB();
        PersistentDB messagesDb = new PersistentDB();
        PubSub messageQueue = new PubSub(scheduler);

        MessageService messageServ = new MessageService(messageQueue);
        SessionManager sessionManag = new SessionManager(sessionsDb);
        List<Gateway> gateways = createGateways(10, messageServ, sessionManag);
        List<Client> clients = createClients(100, gateways);
        DelivererService delivererServ = new DelivererService(messageQueue, gateways, sessionManag);
        MessageStorage messageSt = new MessageStorage(messageQueue, messagesDb);

        CountDownLatch latch = new CountDownLatch(100);
        scheduler.scheduleAtFixedRate(() -> {
            var from = clients.get(random.nextInt(clients.size()));
            var to = clients.get(random.nextInt(clients.size()));
            var message = "message-" + UUID.randomUUID();
            System.out.println("Message from: " + from.getPhone() + ", to: " + to.getPhone() + ", body: " + message);
            from.sendMessage(to.getPhone(), message);
            latch.countDown();
        }, 1, 1, TimeUnit.SECONDS);

        latch.await();
    }

    private List<Gateway> createGateways(int count, MessageService messageServ, SessionManager sessionManag) {
        List<Gateway> list = new ArrayList<>();
        while (count-- > 0) {
            list.add(new Gateway(count, sessionManag, messageServ));
        }
        return list;
    }

    private List<Client> createClients(int count, List<Gateway> gateways) {
        List<Client> list = new ArrayList<>();
        while(count-- > 0) {
            var gt = gateways.get(count % gateways.size());
            var client = new Client(String.valueOf(System.nanoTime()),  gt);
            client.auth();
            list.add(client);
        }
        return list;
    }
}
