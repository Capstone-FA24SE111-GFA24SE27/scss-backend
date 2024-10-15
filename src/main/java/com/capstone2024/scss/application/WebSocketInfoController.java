package com.capstone2024.scss.application;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/information")
@Tag(name = "server-information", description = "Get info of specific config of server.")
public class WebSocketInfoController {

    @Value("${websocket.url}")
    private String websocketUrl;

    @Operation(summary = "Get WebSocket Connection Information", description = "Returns the URL and description of the WebSocket endpoint.")
    @GetMapping("/websocket-info")
    public Map<String, String> getWebSocketInfo() {
        return Map.of(
                "url", websocketUrl,
                "description", "WebSocket connection endpoint for real-time communication.",
                "/user/{account_id}/private/notification", "topic for notification",
                "`/user/{dateChange}/{counselorId}/slot`", "topic for slot realtime",
                "/user/${profileId}/appointment", "topic for appointment schedule realtime",
                "/user/${chatSessionId}/chat", "topic for chatting Q&A"
        );
    }
}
