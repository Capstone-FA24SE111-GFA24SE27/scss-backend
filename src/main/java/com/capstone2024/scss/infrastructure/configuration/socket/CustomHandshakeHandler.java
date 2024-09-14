package com.capstone2024.scss.infrastructure.configuration.socket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomHandshakeHandler.class);

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // Log information about the handshake request
        logger.info("/////////////////////NEW USER CONNECTED SOCKET////////////////////////////");
        logger.info("Determining user for request: {}", request.getURI());

        Principal user = super.determineUser(request, wsHandler, attributes);

        if (user != null) {
            logger.info("User determined: {}", user.getName());
        } else {
            logger.info("No user determined for the request");
        }

        return user;
    }
}
