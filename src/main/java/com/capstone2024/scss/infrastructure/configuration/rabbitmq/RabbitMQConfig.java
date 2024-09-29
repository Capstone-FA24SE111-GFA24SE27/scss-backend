package com.capstone2024.scss.infrastructure.configuration.rabbitmq;

import com.capstone2024.scss.infrastructure.configuration.rabbitmq.receiver.RabbitMQEmailReceiver;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.receiver.RabbitMQNotificationReceiver;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String EMAIL_QUEUE = "emailQueue";
    public static final String NOTIFICATION_QUEUE = "notificationQueue";
    public static final String NOTIFICATION_MOBILE_QUEUE = "notification_mobile_queue";
    public static final String REAL_TIME_COUNSELING_SLOT = "real_time_counseling_slot";
    public static final String REAL_TIME_COUNSELING_APPOINTMENT = "real_time_counseling_appointment";

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, false);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, false);
    }

    @Bean
    public Queue notificationMobileQueue() {
        return new Queue(NOTIFICATION_MOBILE_QUEUE, false);
    }

    @Bean
    public Queue realtimeSlotQueue() {
        return new Queue(REAL_TIME_COUNSELING_SLOT, false);
    }

    @Bean
    public Queue realtimeAppointmentQueue() {
        return new Queue(REAL_TIME_COUNSELING_APPOINTMENT, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleMessageListenerContainer emailListenerContainer(ConnectionFactory connectionFactory,
                                                                 MessageListenerAdapter emailListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(EMAIL_QUEUE);
        container.setMessageListener(emailListenerAdapter);
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer notificationListenerContainer(ConnectionFactory connectionFactory,
                                                                        MessageListenerAdapter notificationListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(NOTIFICATION_QUEUE);
        container.setMessageListener(notificationListenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter emailListenerAdapter(RabbitMQEmailReceiver receiver) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveEmailMessage");
        adapter.setMessageConverter(jsonMessageConverter());
        return adapter;
    }

    @Bean
    public MessageListenerAdapter notificationListenerAdapter(RabbitMQNotificationReceiver receiver) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveNotificationMessage");
        adapter.setMessageConverter(jsonMessageConverter());
        return adapter;
    }
}
