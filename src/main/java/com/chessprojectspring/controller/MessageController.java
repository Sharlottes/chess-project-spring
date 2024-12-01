package com.chessprojectspring.controller;

import com.chessprojectspring.dto.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        return message;
    }

    // 체스 말을 움직이는 메소드 
    @MessageMapping("/move")
    @SendTo("/topic/moves")
    public String movePiece(String move) {
        return move;
    }
} 