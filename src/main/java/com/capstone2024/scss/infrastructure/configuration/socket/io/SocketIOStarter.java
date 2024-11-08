package com.capstone2024.scss.infrastructure.configuration.socket.io;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SocketIOStarter {

    private final SocketIOServer server;

    @Autowired
    public SocketIOStarter(SocketIOServer server) {
        this.server = server;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void startServer() {
        server.start();
        System.out.println("Socket.IO server started on port 9092");

        server.addConnectListener(client -> {
            System.out.println("Client connected: " + client.getSessionId());
        });

        server.addDisconnectListener(client -> {
            System.out.println("Client disconnected: " + client.getSessionId());
        });
    }

    // Đảm bảo dừng server khi ứng dụng dừng
    @PreDestroy
    public void stopServer() {
        server.stop();
        System.out.println("Socket.IO server stopped");
    }
}

