package com.capstone2024.scss.infrastructure.configuration.socket.io;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class NettySocketIOConfig {

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");  // Đổi thành hostname nếu cần
        config.setPort(9092);             // Đặt cổng cho server

        config.setOrigin("*");            // Cho phép tất cả nguồn gốc, hoặc giới hạn nếu cần

        return new SocketIOServer(config);
    }
}


