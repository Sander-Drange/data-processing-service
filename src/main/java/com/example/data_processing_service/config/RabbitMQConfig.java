package com.example.data_processing_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    public static final String HOLDING_CREATED_QUEUE = "holding.created.queue";
    public static final String HOLDING_CREATED_EXCHANGE = "holding.created.exchange";
    public static final String HOLDING_CREATED_ROUTING_KEY = "holding.created";

    public static final String PRICE_REQUEST_QUEUE = "price.request.queue";
    public static final String PRICE_RESPONSE_QUEUE = "price.response.queue";
    public static final String PRICE_UPDATE_QUEUE = "price.update.queue";
    public static final String PRICE_UPDATE_EXCHANGE = "price.update.exchange";
    public static final String PRICE_UPDATE_ROUTING_KEY = "price.update";

    @Bean
    public Queue priceRequestQueue() {
        return new Queue(PRICE_REQUEST_QUEUE);
    }

    @Bean
    public Queue priceResponseQueue() {
        return new Queue(PRICE_RESPONSE_QUEUE);
    }

    @Bean
    public Queue priceUpdateQueue() {
        return new Queue(PRICE_UPDATE_QUEUE);
    }

    @Bean
    public TopicExchange priceExchange() {
        return new TopicExchange(PRICE_UPDATE_EXCHANGE);
    }

    @Bean
    public Binding priceRequestBinding() {
        return BindingBuilder.bind(priceRequestQueue())
                .to(priceExchange())
                .with(PRICE_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Binding priceResponseBinding() {
        return BindingBuilder.bind(priceResponseQueue())
                .to(priceExchange())
                .with(PRICE_UPDATE_ROUTING_KEY);
    }
}