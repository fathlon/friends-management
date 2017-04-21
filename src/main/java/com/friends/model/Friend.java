package com.friends.model;

import java.util.ArrayList;
import java.util.List;

public class Friend {

	private String email;
	private List<String> friends;
	private List<String> following;
	private List<String> block;
	
	public Friend(String email) {
		this.email = email;
		friends = new ArrayList<>();
		following = new ArrayList<>();
		block = new ArrayList<>();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getFriends() {
		return friends;
	}

	public void addFriend(String friend) {
		if(!friends.contains(friend)) {
			friends.add(friend);
		}
	}
	
	public List<String> getFollowing() {
		return following;
	}
	
	public void addFollowing(String friend) {
		if(!following.contains(friend)) {
			following.add(friend);
		}
	}

	public List<String> getBlock() {
		return block;
	}
	
	public void addBlock(String friend) {
		if(!block.contains(friend)) {
			block.add(friend);
		}
	}
	
	public boolean isBlocking(String friend) {
		if(block.contains(friend)) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Friend other = (Friend) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

}
