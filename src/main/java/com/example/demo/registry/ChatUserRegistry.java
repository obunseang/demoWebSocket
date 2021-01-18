package com.example.demo.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.example.demo.model.UserVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatUserRegistry {
	
	private Map<String, UserVO> mUserMap = new HashMap<>();
	private Map<String, UserVO> mUserSessionMap = new HashMap<>();
	private static final String UUID_VALUE = UUID.randomUUID().toString();
	private final Object sessionLock = new Object();
	
	public String getPodId() {
		return UUID_VALUE;
	}
	
	private String getUserId(final StompHeaderAccessor accessor) {
		if (!ObjectUtils.isEmpty(accessor)) {

			List<String> userList = accessor.getNativeHeader("user");
			if (!ObjectUtils.isEmpty(userList) && userList.size() > 0) {
				return userList.get(0);
			}
		}
		return null;
	}

	public Set<UserVO> getUsers() {
		Set<UserVO> users = new HashSet<>(mUserMap.values());
		return users;
	}
	
	public boolean isExist(final String userId) {
		return mUserMap.containsKey(userId);
	}
	
	public int userMapSize() {
		return mUserMap.size();
	}

	public void connectUser(final StompHeaderAccessor accessor) {

		synchronized (this.sessionLock) {
			if (!ObjectUtils.isEmpty(accessor)) {
				final String userId = getUserId(accessor);
				final String sessionId = accessor.getSessionId();
				accessor.getSessionAttributes().put("username", userId);
				if (!ObjectUtils.isEmpty(userId) && !ObjectUtils.isEmpty(sessionId)) {

					UserVO userVO = mUserMap.get(userId);

					if (ObjectUtils.isEmpty(userVO)) {
						final List<String> sessionIdList = new ArrayList<>();
						sessionIdList.add(sessionId);
						userVO = UserVO.builder().userName(userId).sessionIdList(sessionIdList).build();
						mUserMap.put(userId, userVO);
						log.info("##### WS connect user : {}", mUserMap.get(userId).getUserName());
					} else {
						userVO.getSessionIdList().add(sessionId);
					}
					mUserSessionMap.put(sessionId, userVO);

					log.debug("##### WS connect sessionId : {}",
							Arrays.toString(mUserMap.get(userId).getSessionIdList().toArray()));
				}	
			}
		}
	}
	
	public UserVO disconnectUser(final StompHeaderAccessor accessor) {
		UserVO ret = null;
		synchronized (this.sessionLock) {

			if (!ObjectUtils.isEmpty(accessor)) {
				final String sessionId = accessor.getSessionId();

				if (!ObjectUtils.isEmpty(sessionId)) {
					ret = mUserSessionMap.remove(sessionId);
					if (!ObjectUtils.isEmpty(ret)) {
						UserVO userVO = mUserMap.get(ret.getUserName());
						userVO.getSessionIdList().remove(sessionId);

						log.info("##### WS disconnect sessionId : {}", sessionId);

						if (userVO.getSessionIdList().size() == 0) {
							mUserMap.remove(userVO.getUserName());
							log.info("##### WS disconnect user : {}", userVO.getUserName());
						}

					}

				}
			}
		}
		return ret;
	}
}
