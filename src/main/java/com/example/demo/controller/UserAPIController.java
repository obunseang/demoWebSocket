package com.example.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController 
@RequestMapping("/portal/admin/operation/rest/v1")
public class UserAPIController {
	
	@Autowired
    private SimpMessageSendingOperations messagingTemplate;
    
	
	@RequestMapping(method = RequestMethod.POST, path = "/WebSocketAPI")
    public String broadcastMessage(HttpServletRequest request){
		String Destination  = request.getParameter("Destination");
		String MsgContent = request.getParameter("MsgContent");
		if(Destination == null) {
			return "{\"code\":400}";
		}			
		else if(MsgContent == null)
		{
			return "{\"code\":300}";
		}
		else
		{			
			System.out.println(Destination+"========="+MsgContent);
			messagingTemplate.convertAndSend(Destination, MsgContent);
			log.info("-----------------------ok---------------------------");
		}
		return "{\"code\":200}";
    }
}
