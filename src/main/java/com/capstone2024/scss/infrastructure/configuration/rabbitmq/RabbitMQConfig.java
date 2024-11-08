package com.capstone2024.scss.infrastructure.configuration.rabbitmq;

import com.capstone2024.scss.infrastructure.configuration.rabbitmq.receiver.RabbitMQEmailReceiver;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.receiver.RabbitMQListener;
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
    public static final String REAL_TIME_CHAT_SESSION = "REAL_TIME_CHAT_SESSION";
    public static final String REAL_TIME_COUNSELING_APPOINTMENT_REQUEST = "real_time_counseling_appointment_request";
    public static final String REAL_TIME_Q_A = "real_time_q_a";

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
    public Queue realtimeChatSessionQueue() {
        return new Queue(REAL_TIME_CHAT_SESSION, false);
    }

    @Bean
    public Queue realtimeAppointmentRequestQueue() {
        return new Queue(REAL_TIME_COUNSELING_APPOINTMENT_REQUEST, false);
    }

    @Bean
    public Queue realtimeQuestionQueue() {
        return new Queue(REAL_TIME_Q_A, false);
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

    // Cấu hình listener cho các hàng đợi
    @Bean
    public SimpleMessageListenerContainer notificationListener(ConnectionFactory connectionFactory,
                                                               RabbitMQListener rabbitMQListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames("notification_mobile_queue");
        container.setMessageListener(new MessageListenerAdapter(rabbitMQListener, "handleNotificationMessage"));
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer counselingSlotListener(ConnectionFactory connectionFactory,
                                                                 RabbitMQListener rabbitMQListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames("real_time_counseling_slot");
        container.setMessageListener(new MessageListenerAdapter(rabbitMQListener, "handleCounselingSlotMessage"));
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer counselingAppointmentListener(ConnectionFactory connectionFactory,
                                                                        RabbitMQListener rabbitMQListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames("real_time_counseling_appointment");
        container.setMessageListener(new MessageListenerAdapter(rabbitMQListener, "handleCounselingAppointmentMessage"));
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer questionListener(ConnectionFactory connectionFactory,
                                                           RabbitMQListener rabbitMQListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames("real_time_q_a");
        container.setMessageListener(new MessageListenerAdapter(rabbitMQListener, "handleQAMessage"));
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer appointmentRequestListener(ConnectionFactory connectionFactory,
                                                                     RabbitMQListener rabbitMQListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames("real_time_counseling_appointment_request");
        container.setMessageListener(new MessageListenerAdapter(rabbitMQListener, "handleAppointmentRequest"));
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer chatSessionListener(ConnectionFactory connectionFactory,
                                                              RabbitMQListener rabbitMQListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames("REAL_TIME_CHAT_SESSION");
        container.setMessageListener(new MessageListenerAdapter(rabbitMQListener, "handleChatSession"));
        return container;
    }

//    @Bean
//    public SimpleMessageListenerContainer emailListenerContainer(ConnectionFactory connectionFactory,
//                                                                 MessageListenerAdapter emailListenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(EMAIL_QUEUE);
//        container.setMessageListener(emailListenerAdapter);
//        return container;
//    }
//
//    @Bean
//    public SimpleMessageListenerContainer notificationListenerContainer(ConnectionFactory connectionFactory,
//                                                                        MessageListenerAdapter notificationListenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(NOTIFICATION_QUEUE);
//        container.setMessageListener(notificationListenerAdapter);
//        return container;
//    }
//
//    @Bean
//    public MessageListenerAdapter emailListenerAdapter(RabbitMQEmailReceiver receiver) {
//        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveEmailMessage");
//        adapter.setMessageConverter(jsonMessageConverter());
//        return adapter;
//    }
//
//    @Bean
//    public MessageListenerAdapter notificationListenerAdapter(RabbitMQNotificationReceiver receiver) {
//        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveNotificationMessage");
//        adapter.setMessageConverter(jsonMessageConverter());
//        return adapter;
//    }
}
