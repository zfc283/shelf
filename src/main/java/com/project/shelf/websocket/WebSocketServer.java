package com.project.shelf.websocket;

import com.project.shelf.util.SnowFlake;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
@ServerEndpoint("/api/ws/{token}")
@Slf4j
public class WebSocketServer {

    // WebSocket 实现类是多实例的, 无法通过属性依赖注入
    private static SnowFlake snowFlake;

    @Resource
    public void setSnowFlake(SnowFlake snowFlake1) {
        snowFlake = snowFlake1;
    }

    /**
     * 每个客户端一个token
     */
    private String token = "";

    private static HashMap<String, Session> map = new HashMap<>();      // 推送消息需要 session, 连接后需要保存 session

    /**
     * 连接成功
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        map.put(token, session);
        this.token = token;
        log.info("有新连接：token：{}，session id：{}，当前连接数：{}", token, session.getId(), map.size());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session) {
        map.remove(this.token);
        log.info("连接关闭，token：{}，session id：{}！当前连接数：{}", this.token, session.getId(), map.size());
    }

    /**
     * 收到消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到消息：{}，内容：{}", token, message);
    }

    /**
     * 连接错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误", error);
    }

    /**
     * 群发消息
     */
    public void sendInfo(String message) {
        for (String token : map.keySet()) {
            Session session = map.get(token);
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("推送消息失败：{}，内容：{}", token, message);
            }
            log.info("推送消息：{}，内容：{}", token, message);
        }
    }

}
