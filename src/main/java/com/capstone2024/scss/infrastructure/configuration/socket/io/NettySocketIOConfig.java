package com.capstone2024.scss.infrastructure.configuration.socket.io;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@org.springframework.context.annotation.Configuration
@Slf4j
public class NettySocketIOConfig {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",
            "https://scss-fe.azurewebsites.net",
            "http://scss-fe.azurewebsites.net"
    );

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");  // Đổi thành hostname nếu cần
        config.setPort(9092);             // Đặt cổng cho server

//        config.setOrigin("http://localhost:3000");

        // Kiểm tra origin trong quá trình bắt tay (handshake)
        config.setAuthorizationListener((HandshakeData handshakeData) -> {
//            String origin = handshakeData.getHttpHeaders().get("Origin");
//            boolean isValid = origin == null || ALLOWED_ORIGINS.contains(origin);
//
//            if (!isValid) {
//                log.info("Blocked connection from origin: " + origin);
//            }

            return true;
        });

        return new SocketIOServer(config);
    }
}


