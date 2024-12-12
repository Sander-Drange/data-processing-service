package com.example.data_processing_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String PRICE_UPDATE_QUEUE = "price.update.queue";
    public static final String PRICE_UPDATE_EXCHANGE = "price.update.exchange";
    public static final String PRICE_UPDATE_ROUTING_KEY = "price.update";

    @Bean
    public Queue priceUpdateQueue() {
        return new Queue(PRICE_UPDATE_QUEUE);
    }

    @Bean
    public TopicExchange priceUpdateExchange() {
        return new TopicExchange(PRICE_UPDATE_EXCHANGE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(priceUpdateQueue())
                .to(priceUpdateExchange())
                .with(PRICE_UPDATE_ROUTING_KEY);
    }
}