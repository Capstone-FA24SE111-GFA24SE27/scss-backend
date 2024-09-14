package com.capstone2024.scss.infrastructure.configuration.rabbitmq.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQEmailReceiver {

    @Autowired
    private JavaMailSender mailSender;

    public void receiveEmailMessage(String emailMessage) {
        // Logic gửi email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("recipient@example.com");
        message.setSubject("Test Mail from RabbitMQ");
        message.setText(emailMessage);
        mailSender.send(message);
        System.out.println("Email sent successfully!");
    }
}
