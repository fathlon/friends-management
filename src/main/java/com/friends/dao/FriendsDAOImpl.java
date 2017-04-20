package com.friends.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.friends.model.Friend;

@Repository
public class FriendsDAOImpl implements FriendsDAO {
	
	private Map<String, Friend> friendsDB = new ConcurrentHashMap<>();

	@Override
	public void addFriend(String friendEmail1, String friendEmail2) {
		Friend friend1 = friendsDB.getOrDefault(friendEmail1, new Friend(friendEmail1));
		Friend friend2 = friendsDB.getOrDefault(friendEmail2, new Friend(friendEmail2));
		
		friend1.addFriend(friend2);
		friend2.addFriend(friend1);
		
		friendsDB.put(friend1.getEmail(), friend1);
		friendsDB.put(friend2.getEmail(), friend2);
	}

}
