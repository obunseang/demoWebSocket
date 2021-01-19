package com.example.demo.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.UserVO;
import com.example.demo.model.ChatMessage.MessageType;
import com.example.demo.model.ContractMessage;
import com.example.demo.registry.ChatUserRegistry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@Configuration
public class SchedulerAlarm {

	@Autowired
	private SimpMessagingTemplate template;
	@Autowired
    private ChatUserRegistry mUserRegistry;
	
	@Scheduled(fixedDelay = 10000)
	public void sendAlarmMesssage() {
		if(mUserRegistry.userMapSize() > 0)
		{				
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
			Date date = new Date();  
			String datetime = formatter.format(date);
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setType(MessageType.CHAT);
			chatMessage.setSender("Server Alarm");
			chatMessage.setContent(datetime);
			Set<UserVO> userList = mUserRegistry.getUsers();
			Iterator<UserVO> iter = userList.iterator();
			
			ContractMessage msg = new ContractMessage();
			msg.setContractNo("N5670413");
			msg.setCrtNo("4160001-00");
			msg.setStatus("1");
			
			while(iter.hasNext()) {
				UserVO user = iter.next();			    
				template.convertAndSend("/subscribe/"+user.getUserName(), chatMessage);
				template.convertAndSend("/subscribe/"+user.getUserName(), msg);
			}
			log.info("==========================Server sent alarm message at {}============================",datetime);
		}
	}
}
