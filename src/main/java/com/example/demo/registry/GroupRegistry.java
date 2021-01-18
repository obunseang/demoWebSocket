package com.example.demo.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.example.demo.model.UserVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GroupRegistry {
	
	private Map<String, UserVO> mGroupMap = new HashMap<>();
	
	private String getUserId(final StompHeaderAccessor accessor) {
		if (!ObjectUtils.isEmpty(accessor)) {
			List<String> userList = accessor.getNativeHeader("user");
			if (!ObjectUtils.isEmpty(userList) && userList.size() > 0) {
				return userList.get(0);
			}
		}
		return null;
	}
}
