package com.example.demo.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatMessage.MessageType;

@Component
public class WSEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WSEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userName = (String) accessor.getSessionAttributes().get("username");
		if(userName!=null && !userName.isEmpty()) {						
	        for( Map.Entry<String, Object> elem : accessor.getSessionAttributes().entrySet() ){
	        	if(!elem.getKey().equals(userName) && !elem.getKey().contains(userName)){
	        		String subGroupDest = (String)elem.getValue();
	        		logger.info("User : "+userName+" Disconnected : "+subGroupDest);	         
					ChatMessage chatMessage = new ChatMessage();
					chatMessage.setType(MessageType.LEAVE);
					chatMessage.setSender(userName);
					messagingTemplate.convertAndSend(subGroupDest, chatMessage);		          
	        		logger.info( String.format("key : %s, value : %s", elem.getKey(), subGroupDest) );	            
	        	}
	        }
		}		
    }
}