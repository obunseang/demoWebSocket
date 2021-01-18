package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.example.demo.controller.ChatController;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatMessage.MessageType;
import com.example.demo.model.UserVO;
import com.example.demo.registry.ChatUserRegistry;
import com.example.demo.registry.GroupRegistry;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class WSHandler  implements ChannelInterceptor {
	@Autowired
    private ChatUserRegistry mUserRegistry;
	@Autowired
	private GroupRegistry mGroupRegistry;
	
	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String sessionId = accessor.getSessionId();
        log.info("\r\n" + "SessionID: "+sessionId+" Accessor: "+accessor.toString());
        switch (accessor.getCommand()) {
            case CONNECT:
                // 유저가 Websocket으로 connect()를 한 뒤 호출됨
            	log.info("Stomp Handler CONNECT");
                mUserRegistry.connectUser(accessor);
                break;
            case DISCONNECT:
                // 유저가 Websocket으로 disconnect() 를 한 뒤 호출됨 or 세션이 끊어졌을 때 발생함(페이지 이동~ 브라우저 닫기 등)
            	log.info("Stomp Handler DISCONNECT");
            	mGroupRegistry.ClearDisConUser(accessor);
                mUserRegistry.disconnectUser(accessor);
                break;
            case SUBSCRIBE:
            	mGroupRegistry.setSubGroup(accessor);
                break;
            case UNSUBSCRIBE:
            	mGroupRegistry.setUnSubGroup(accessor);
            	break;
            case MESSAGE:
            	log.info("Stomp Handler MESSAGE");
                break;
            default:
            	log.info("Stomp Handler Default Case");
                break;
        }
	}
}
