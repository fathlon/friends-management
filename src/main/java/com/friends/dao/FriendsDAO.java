package com.friends.dao;

import java.util.List;

import com.friends.exception.EmailNotFoundException;

public interface FriendsDAO {

	public boolean addFriend(String friendEmail1, String friendEmail2);

	public List<String> getFriendList(String email) throws EmailNotFoundException;

	public boolean follow(String requestorEmail, String targetEmail);

	public boolean block(String requestorEmail, String targetEmail);

}
