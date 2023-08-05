package com.devaffeine.building.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


public class PubSub {
    public class Subscriber {
        Function<List<Object>, Boolean> callback;

        int position;

        public Subscriber(Function<List<Object>, Boolean> callback, int position) {
            this.callback = callback;
            this.position = position;
        }
    }

    private Map<String, ArrayList<Object>> queues = new ConcurrentHashMap<>();

    private Map<String, List<Subscriber>> subscribers = new ConcurrentHashMap<>();

    public PubSub(ScheduledExecutorService scheduler) {
        scheduler.scheduleAtFixedRate(this::deliverMessages, 1, 1, TimeUnit.SECONDS);
    }

    public void publish(String topic, Object object) {
        queues.computeIfAbsent(topic, k -> new ArrayList<>()).add(object);
    }

    public void subscribe(String topic, int position, Function<List<Object>, Boolean> subs) {
        subscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(new Subscriber(subs, position));
    }

    private void deliverMessages() {
        try {
            System.out.println("delivering queue messages");
            for (Map.Entry<String, List<Subscriber>> topic : subscribers.entrySet()) {
                var log = queues.get(topic.getKey());
                if(log == null) {
                    continue;
                }
                for (Subscriber subscriber : topic.getValue()) {
                    int size = log.size();
                    if (subscriber.position < size - 1) {
                        var list = new ArrayList<>();
                        for (int i = subscriber.position; i < size; i++) {
                            list.add(log.get(i));
                        }
                        if (!list.isEmpty()) {
                            System.out.println("delivering " + list.size() + " messages");
                            var result = subscriber.callback.apply(list);
                            if (result != null && result) {
                                subscriber.position += list.size();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
