package com.friends.service;

import java.util.List;

import com.friends.exception.InvalidParamException;

public interface FriendsService {

	public boolean addFriend(List<String> friends) throws InvalidParamException;
}
