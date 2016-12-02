package com.suhailkandanur.fresco.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by suhail on 2016-12-01.
 */
@Configuration
public class RabbitConfiguration {

    @Bean
    FanoutExchange fanoutExchange() {
        return (FanoutExchange) ExchangeBuilder.fanoutExchange("fresco").build();
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable("fresco-request").build();
    }

    @Bean
    Binding binding(Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }
}
