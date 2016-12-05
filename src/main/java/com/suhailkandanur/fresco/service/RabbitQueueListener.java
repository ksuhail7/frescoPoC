package com.suhailkandanur.fresco.service;

/**
 * Created by suhail on 2016-12-02.
 */
public interface RabbitQueueListener {
    void processMessage(String message) throws Exception ;
}
