package com.capstone2024.scss.infrastructure.configuration.rabbitmq.receiver;

import com.capstone2024.scss.application.q_and_a.dto.MessageDTO;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeAppointmentDTO;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeAppointmentRequestDTO;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeCounselingSlotDTO;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeQuestionDTO;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@EnableRabbit
public class RabbitMQListener {

    private final SocketIOServer socketIOServer;
    private final ObjectMapper objectMapper;

    @Autowired
    public RabbitMQListener(SocketIOServer socketIOServer, ObjectMapper objectMapper) {
        this.socketIOServer = socketIOServer;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "notification_mobile_queue")
    public void handleNotificationMessage(byte[] message) {
        try {
            String jsonMessage = new String(message);
            RealTimeAppointmentDTO notificationMessage = objectMapper.readValue(jsonMessage, RealTimeAppointmentDTO.class);
            System.out.println("Received message from RabbitMQ: " + notificationMessage);
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + notificationMessage.getStudentId() + "/private/notification", notificationMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "real_time_counseling_slot")
    public void handleCounselingSlotMessage(byte[] message) {
        try {
            String jsonMessage = new String(message);
            RealTimeCounselingSlotDTO slotMessage = objectMapper.readValue(jsonMessage, RealTimeCounselingSlotDTO.class);
            System.out.println("Received message from RabbitMQ: " + slotMessage);
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + slotMessage.getDateChange() + "/" + slotMessage.getCounselorId() + "/slot", slotMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "real_time_counseling_appointment")
    public void handleCounselingAppointmentMessage(byte[] message) {
        try {
            String jsonMessage = new String(message);
            RealTimeAppointmentDTO appointmentMessage = objectMapper.readValue(jsonMessage, RealTimeAppointmentDTO.class);
            System.out.println("Received message from RabbitMQ: " + appointmentMessage);
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + appointmentMessage.getStudentId() + "/appointment", "Update");
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + appointmentMessage.getCounselorId() + "/appointment", "Update");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "real_time_q_a")
    public void handleQAMessage(byte[] message) {
        try {
            String jsonMessage = new String(message);
            RealTimeQuestionDTO qaMessage = objectMapper.readValue(jsonMessage, RealTimeQuestionDTO.class);
            System.out.println("Received message from RabbitMQ: " + qaMessage);
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + qaMessage.getStudentId() + "/question", qaMessage);
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + qaMessage.getCounselorId() + "/question", qaMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "real_time_counseling_appointment_request")
    public void handleAppointmentRequest(byte[] message) {
        try {
            String jsonMessage = new String(message);
            RealTimeAppointmentRequestDTO requestMessage = objectMapper.readValue(jsonMessage, RealTimeAppointmentRequestDTO.class);
            System.out.println("Received message from RabbitMQ: " + requestMessage);
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + requestMessage.getStudentId() + "/appointment/request", requestMessage);
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + requestMessage.getCounselorId() + "/appointment/request", requestMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "REAL_TIME_CHAT_SESSION")
    public void handleChatSession(byte[] message) {
        try {
            String jsonMessage = new String(message);
            MessageDTO chatMessage = objectMapper.readValue(jsonMessage, MessageDTO.class);
            System.out.println("Received message from RabbitMQ: " + chatMessage);
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + chatMessage.getChatSessionId() + "/chat", chatMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


