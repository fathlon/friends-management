package com.friends.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.friends.exception.EmailNotFoundException;
import com.friends.model.Friend;

@Repository
public class FriendsDAOImpl implements FriendsDAO {
	
	private Map<String, Friend> friendsDB = new ConcurrentHashMap<>();

	@Override
	public boolean addFriend(String friendEmail1, String friendEmail2) {
		Friend friend1 = friendsDB.getOrDefault(friendEmail1, new Friend(friendEmail1));
		Friend friend2 = friendsDB.getOrDefault(friendEmail2, new Friend(friendEmail2));
		
		if(friend1.isBlocking(friendEmail2) || friend2.isBlocking(friendEmail1)) {
			return false;
		}
		
		friend1.addFriend(friendEmail2);
		friend2.addFriend(friendEmail1);
		
		friendsDB.put(friend1.getEmail(), friend1);
		friendsDB.put(friend2.getEmail(), friend2);
		
		return true;
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

	@Override
	public boolean block(String requestorEmail, String targetEmail) {
		Friend requestor = friendsDB.getOrDefault(requestorEmail, new Friend(requestorEmail));
		Friend target = friendsDB.getOrDefault(targetEmail, new Friend(targetEmail));
		
		requestor.addBlock(target.getEmail());

		friendsDB.put(requestor.getEmail(), requestor);
		friendsDB.put(target.getEmail(), target);
		
		return true;
	}

	@Override
	public List<String> findAllAllowedFriends(String senderEmail, List<String> splitText) {
		Set<String> allowedFriends = new HashSet<>();
		for (Entry<String, Friend> entry : friendsDB.entrySet()) {
			Friend current = entry.getValue();
			if(!current.isBlocking(senderEmail) && 
					(current.isFriend(senderEmail) || current.isFollowing(senderEmail) || splitText.contains(current.getEmail()))
					) {
				allowedFriends.add(current.getEmail());
			}
		}
		return new ArrayList<>(allowedFriends);
	}

}
