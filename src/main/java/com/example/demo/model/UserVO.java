package com.example.demo.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserVO {
	private String userName;
	private List<String> sessionIdList;

	@Override
	public int hashCode() {
		if (userName == null)
			return 0;
		return userName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserVO && this.userName != null) {
			final UserVO stompUser = (UserVO) obj;
			return this.userName.equals(stompUser.getUserName());
		}
		return false;
	}
}
