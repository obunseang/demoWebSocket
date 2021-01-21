package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.demo.model.ChatMessage;

@Controller
public class ChatController {

	public final SimpMessagingTemplate template;
	
	@Autowired
    public ChatController(SimpMessagingTemplate template) {
        this.template = template;
    }
	
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
    	 template.convertAndSend("/subscribe/contract/" + chatMessage.getGroup(), chatMessage);
    }
}
