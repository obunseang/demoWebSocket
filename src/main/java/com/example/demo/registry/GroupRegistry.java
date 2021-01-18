package com.example.demo.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.User;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.example.demo.model.UserVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GroupRegistry {
	
	private Map<String, List<UserVO>> mGroupMap = new HashMap<>();
	public void setSubGroup(final StompHeaderAccessor accessor) {
		String subGroupDest = accessor.getDestination();
		accessor.getSessionAttributes().put(accessor.getSubscriptionId(), subGroupDest);
		String UserName = (String) accessor.getSessionAttributes().get("username");
		if(!subGroupDest.isEmpty() && !UserName.isEmpty()) {
			List<UserVO> list = mGroupMap.get(subGroupDest);			
			if(list == null)
			{
				list = new ArrayList<>();
				UserVO u = UserVO.builder().userName(UserName).build();
				list.add(u);
				mGroupMap.put(subGroupDest, list);
			}
			else
			{
				boolean found = false;
				for(int i=0;i<list.size();i++) {
					UserVO isUser = list.get(i);
					if(isUser.getUserName().equals(UserName)){
						found = true;
						break;
					}
				}
				if(!found)
				{					
					UserVO u = UserVO.builder().userName(UserName).build();
					list.add(u);
				}
			}
		}
	}	
	public void setUnSubGroup(final StompHeaderAccessor accessor) {
		String subscriptionId = accessor.getSubscriptionId();
		String subGroupDest = (String) accessor.getSessionAttributes().get(subscriptionId);
		accessor.getSessionAttributes().remove(accessor.getSubscriptionId());
		String UserName = (String) accessor.getSessionAttributes().get("username");
		if(subGroupDest!= null && !subGroupDest.isEmpty())
		{			
			List<UserVO> list = mGroupMap.get(subGroupDest);
			if(list != null)
			{
				for(int i=0;i<list.size();i++) {
					UserVO u = list.get(i);
					if(u.getUserName().equals(UserName)){
						list.remove(i);
						break;
					}
				}
			}
			if(list.size() == 0) {		
				mGroupMap.remove(subGroupDest);
			}
		}			
	}
	public void ClearDisConUser(final StompHeaderAccessor accessor) {
		String userName = (String) accessor.getSessionAttributes().get("username");
		if(userName!=null && !userName.isEmpty()) {						
	        for( Map.Entry<String, Object> elem : accessor.getSessionAttributes().entrySet() ){
	        	if(!elem.getKey().equals(userName))
	        	{
	        		String subGroupDest = (String)elem.getValue();
	        		List<UserVO> list = mGroupMap.get(subGroupDest);
	    			if(list != null)
	    			{
	    				for(int i=0;i<list.size();i++) {
	    					UserVO u = list.get(i);
	    					if(u.getUserName().equals(userName)){
	    						list.remove(i);
	    						break;
	    					}
	    				}
	    			}	            
	        	}
	        }
		}		
	}
}
