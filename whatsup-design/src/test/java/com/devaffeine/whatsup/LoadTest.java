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
        DelivererService delivererServ = new DelivererService(messageQueue, gateways, sessionManag, messagesDb);
        gateways.forEach(x -> x.setDeliverer(delivererServ));
        MessageStorage messageSt = new MessageStorage(messageQueue, messagesDb);
        List<Client> clients = createClients(10, gateways);

        List<Client> disconnected = new ArrayList<>();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                synchronized (disconnected) {
                    var client = clients.get(random.nextInt(clients.size()));
                    disconnected.add(client);
                    clients.remove(client);
                    client.disconnect();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 10, 500, TimeUnit.MILLISECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                synchronized (disconnected) {
                    System.out.println("connecting disconnected clients");
                    var gateway = gateways.get(random.nextInt(gateways.size()));
                    disconnected.forEach(x -> {
                        x.connect(gateway);
                        x.auth();
                        clients.add(x);
                    });
                    disconnected.clear();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 100, 1000, TimeUnit.MILLISECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            synchronized (disconnected) {
                var from = clients.get(random.nextInt(clients.size()));
                var to = clients.get(random.nextInt(clients.size()));
                var message = "message-" + UUID.randomUUID();
                System.out.println("Message from: " + from.getPhone() + ", to: " + to.getPhone() + ", body: " + message);
                from.sendMessage(to.getPhone(), message);
            }
        }, 1, 10, TimeUnit.MILLISECONDS);

        CountDownLatch finish = new CountDownLatch(1);
        scheduler.scheduleAtFixedRate(() -> {
            int count = messagesDb.getCount("messages");
            System.out.println("database stored messages: " + count);
            if(count == 1000) {
                finish.countDown();
            }
        }, 1, 1, TimeUnit.SECONDS);

        finish.await();

        int totalSent = 0;
        int totalReceived = 0;
        for (var client : clients) {
            var contacts = client.getIteractions();
            if(!contacts.isEmpty()) {
                System.out.println("Client " + client.getPhone());
                System.out.println("===============================");
                for (var contact : contacts) {
                    totalSent += client.getSent(contact);
                    totalReceived += client.getReceived(contact);
                    System.out.println(" - " + contact + ", sent: " + client.getSent(contact) + ", received: " + client.getReceived(contact));
                }
            }
        }

        for (var gateway : gateways) {
            System.out.println("Gateway " + gateway.getId());
            System.out.println("===============================");
            System.out.println(" OfflineDeliverCount: " + gateway.getOfflineDeliverCount());
        }

        System.out.println("===============================");
        System.out.println(" Total sent: " + totalSent + ", Total received: " + totalReceived);
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
