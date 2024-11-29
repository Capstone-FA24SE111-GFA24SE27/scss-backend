package com.capstone2024.scss.infrastructure.configuration.rabbitmq.receiver;

import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.application.q_and_a.dto.MessageDTO;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.*;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
//@EnableRabbit
public class RabbitMQListener {

    private final SocketIOServer socketIOServer;
    private final ObjectMapper objectMapper;

    @Autowired
    public RabbitMQListener(SocketIOServer socketIOServer, ObjectMapper objectMapper) {
        this.socketIOServer = socketIOServer;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "notification_mobile_queue")
    public void handleNotificationMessage(NotificationDTO notificationMessage) {
        try {
            System.out.println("Received message from RabbitMQ: " + notificationMessage);
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + notificationMessage.getReceiverId() + "/private/notification", notificationMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "real_time_counseling_slot")
    public void handleCounselingSlotMessage(RealTimeCounselingSlotDTO slotMessage) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateString = slotMessage.getDateChange().format(formatter);
            System.out.println("Received message from RabbitMQ: " + slotMessage);
            RealTimeCounselingSlotMesageDTO message = RealTimeCounselingSlotMesageDTO.builder()
                    .dateChange(dateString)
                    .slotId(slotMessage.getSlotId())
                    .counselorId(slotMessage.getCounselorId())
                    .newStatus(slotMessage.getNewStatus())
                    .studentId(slotMessage.getStudentId())
                    .build();
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + dateString + "/" + slotMessage.getCounselorId() + "/slot", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "real_time_counseling_appointment")
    public void handleCounselingAppointmentMessage(RealTimeAppointmentDTO appointmentMessage) {
        try {
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + appointmentMessage.getStudentId() + "/appointment", "Update");
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + appointmentMessage.getCounselorId() + "/appointment", "Update");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "real_time_q_a")
    public void handleQAMessage(RealTimeQuestionDTO qaMessage) {
        try {
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + qaMessage.getStudentId() + "/question", qaMessage);
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + qaMessage.getCounselorId() + "/question", qaMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "real_time_counseling_appointment_request")
    public void handleAppointmentRequest(RealTimeAppointmentRequestDTO requestMessage) {
        try {
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + requestMessage.getStudentId() + "/appointment/request", requestMessage);
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + requestMessage.getCounselorId() + "/appointment/request", requestMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "REAL_TIME_CHAT_SESSION")
    public void handleChatSession(MessageDTO chatMessage) {
        try {
            socketIOServer.getBroadcastOperations().sendEvent("/user/" + chatMessage.getChatSessionId() + "/chat", chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


