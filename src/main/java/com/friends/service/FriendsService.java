package com.friends.service;

import java.util.List;

import com.friends.exception.EmailNotFoundException;
import com.friends.exception.InvalidParamException;

public interface FriendsService {

	public boolean addFriend(List<String> friends) throws InvalidParamException;

	public List<String> getFriendList(String email) throws InvalidParamException, EmailNotFoundException;

	public List<String> getMutualFriendList(List<String> friends) throws InvalidParamException, EmailNotFoundException;
}
