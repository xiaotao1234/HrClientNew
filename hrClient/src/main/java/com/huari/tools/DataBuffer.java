package com.huari.tools;

import java.util.Queue;

public class DataBuffer<T> {
    Queue<T> queue;
    int size;

    public DataBuffer(Queue<T> queue, int size) {
        this.queue = queue;
        this.size = size;
    }

    public void enqueue(T t) {
        if(queue.size() < 50) {
            queue.offer(t);
        }else {
            queue.poll();
            queue.offer(t);
        }
    }

    public T get(){
       return queue.poll();
    }
}
