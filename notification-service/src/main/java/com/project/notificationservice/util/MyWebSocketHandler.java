package com.project.notificationservice.util;

import com.project.notificationservice.service.WebSocketService;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MyWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketService webSocketService;

    public MyWebSocketHandler(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Connected: " + session);
        Map<String, String> queryParams = getQueryParams(session.getUri());

        String userId = queryParams.get("id");
        System.out.println("Connected id: " + userId);
        webSocketService.addSession(userId, session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // To handle incoming messages just in case
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, String> queryParams = getQueryParams(session.getUri());
        String userId = queryParams.get("id");
        webSocketService.removeSession(userId);
        super.afterConnectionClosed(session, status);
    }


    private Map<String, String> getQueryParams(URI uri) {
        if (uri == null || uri.getQuery() == null) {
            return Map.of();
        }
        return Arrays.stream(uri.getQuery().split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(p -> p[0], p -> p.length > 1 ? p[1] : ""));
    }
}
