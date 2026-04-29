package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public Map<String, Object> handleChatMessage(Map<String, Object> message) {
        log.info("Chat message received: {}", message.get("message"));
        return message;
    }

    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public Map<String, Object> handleTyping(Map<String, Object> message) {
        return message;
    }
}