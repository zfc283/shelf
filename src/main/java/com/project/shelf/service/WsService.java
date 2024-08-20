package com.project.shelf.service;

import com.project.shelf.websocket.WebSocketServer;
import jakarta.annotation.Resource;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WsService {
    @Resource
    private WebSocketServer webSocketServer;

    @Async
    public void sendInfo(String message, String logID) {
        MDC.put("LOG_ID", logID);
        webSocketServer.sendInfo(message);
    }
}
