package com.friends.service;

import java.util.List;

import com.friends.exception.EmailNotFoundException;
import com.friends.exception.InvalidParamException;

public interface FriendsService {

	public boolean addFriend(List<String> friends) throws InvalidParamException;

	public List<String> getFriendList(String email) throws InvalidParamException, EmailNotFoundException;

	public List<String> getMutualFriendList(List<String> friends) throws InvalidParamException, EmailNotFoundException;

	public boolean follow(String requestor, String target) throws InvalidParamException;

	public boolean block(String requestor, String target) throws InvalidParamException;

	public List<String> findAllAllowedFriends(String sender, String text) throws InvalidParamException;
}
