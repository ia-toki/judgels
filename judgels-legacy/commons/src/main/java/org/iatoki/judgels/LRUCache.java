package org.iatoki.judgels;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LRUCache<K, V> {

    private int maxSize;
    private int currentSize;
    private Node<K, V> leastRecentlyUsed;
    private Node<K, V> mostRecentlyUsed;
    private Map<K, Node<K, V>> cache;

    public LRUCache(int maxSize) {
        this.maxSize = maxSize;
        this.currentSize = 0;
        this.leastRecentlyUsed = new Node<>(null, null, null, null);
        this.mostRecentlyUsed = leastRecentlyUsed;
        this.cache = new ConcurrentHashMap<>();
    }

    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    public V get(K key) {
        if (cache.containsKey(key)) {
            Node<K, V> node = cache.get(key);
            if ((node.key == leastRecentlyUsed.key) && (node.key != mostRecentlyUsed.key)) {
                leastRecentlyUsed = node.next;
                leastRecentlyUsed.previous = null;
            } else if (node.key != mostRecentlyUsed.key) {
                node.previous.next = node.next;
                node.next.previous = node.previous;
            }

            if (node.key != mostRecentlyUsed.key) {
                node.previous = mostRecentlyUsed;
                mostRecentlyUsed.next = node;
                mostRecentlyUsed = node;
                mostRecentlyUsed.next = null;
            }
            return node.value;
        } else {
            return null;
        }
    }

    public void put(K key, V value) {
        if (cache.containsKey(key)) {
            this.get(key);
            Node<K, V> node = cache.get(key);
            node.value = value;
        } else {
            Node<K, V> newNode = new Node<>(null, mostRecentlyUsed, key, value);
            mostRecentlyUsed.next = newNode;
            mostRecentlyUsed = newNode;
            cache.put(key, newNode);

            if (currentSize == maxSize) {
                cache.remove(leastRecentlyUsed.key);
                leastRecentlyUsed = leastRecentlyUsed.next;
                leastRecentlyUsed.previous = null;
            } else if (currentSize < maxSize) {
                if (currentSize == 0) {
                    leastRecentlyUsed = newNode;
                }
                currentSize++;
            }
        }
    }

    class Node<A, B> {
        private Node<A, B> next;
        private Node<A, B> previous;
        private A key;
        private B value;

        public Node(Node<A, B> next, Node<A, B> previous, A key, B value) {
            this.next = next;
            this.previous = previous;
            this.key = key;
            this.value = value;
        }
    }
}
