package com.friends.dao;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.friends.exception.EmailNotFoundException;
import com.friends.model.Friend;

@Repository
public class FriendsDAOImpl implements FriendsDAO {
	
	private Map<String, Friend> friendsDB = new ConcurrentHashMap<>();

	@Override
	public void addFriend(String friendEmail1, String friendEmail2) {
		Friend friend1 = friendsDB.getOrDefault(friendEmail1, new Friend(friendEmail1));
		Friend friend2 = friendsDB.getOrDefault(friendEmail2, new Friend(friendEmail2));
		
		friend1.addFriend(friendEmail2);
		friend2.addFriend(friendEmail1);
		
		friendsDB.put(friend1.getEmail(), friend1);
		friendsDB.put(friend2.getEmail(), friend2);
	}

	@Override
	public List<String> getFriendList(String email) throws EmailNotFoundException {
		Friend friend = friendsDB.get(email);
		if (friend == null) {
			throw new EmailNotFoundException();
		}
		return friend.getFriends();
	}

	@Override
	public boolean follow(String requestorEmail, String targetEmail) {
		Friend requestor = friendsDB.getOrDefault(requestorEmail, new Friend(requestorEmail));
		Friend target = friendsDB.getOrDefault(targetEmail, new Friend(targetEmail));
		
		requestor.addFollowing(target.getEmail());
		
		friendsDB.put(requestor.getEmail(), requestor);
		friendsDB.put(target.getEmail(), target);
		
		return true;
	}

}
