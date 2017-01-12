package com.example.baodi.innovation2.parser;

/**
 * Created by zhou on 16-7-22.
 * LineQueue的消费者
 */
public interface QueueConsumer {


    void setQueueProvider(QueueProvider provider);

    /**
     * LineQueue提供器
     */
    interface QueueProvider {
        LineQueue getQueue();
    }
}
