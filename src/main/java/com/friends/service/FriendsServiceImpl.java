package com.friends.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.friends.dao.FriendsDAO;
import com.friends.exception.EmailNotFoundException;
import com.friends.exception.InvalidParamException;

@Service
public class FriendsServiceImpl implements FriendsService {
	
	@Autowired
	protected FriendsDAO friendsDAO;

	@Override
	public boolean addFriend(List<String> friends) throws InvalidParamException {
		String friend1, friend2;
		try {
			if(friends.size() != 2) {
				throw new InvalidParamException();
			}
			
			friend1 = (String) friends.get(0);
			friend2 = (String) friends.get(1);
			
			if(friend1.equals(friend2)) {
				return false;
			}
			
			friendsDAO.addFriend(friend1, friend2);
			return true;
			
		} catch(ClassCastException cce) {
			throw new InvalidParamException("ClassCastException");
		}
	}

	@Override
	public List<String> getFriendList(String email) throws InvalidParamException, EmailNotFoundException {
		if(StringUtils.isBlank(email)) {
			throw new InvalidParamException("String is blank");
		}
		
		return friendsDAO.getFriendList(email);
	}

	@Override
	public List<String> getMutualFriendList(List<String> friends) throws InvalidParamException, EmailNotFoundException {
		String friendEmail1, friendEmail2;
		try {
			if(friends.size() != 2) {
				throw new InvalidParamException();
			}
			
			friendEmail1 = (String) friends.get(0);
			friendEmail2 = (String) friends.get(1);
			
			List<String> friendList1 = getFriendList(friendEmail1);
			List<String> friendList2 = getFriendList(friendEmail2);
			List<String> resultList = new ArrayList<>(friendList1);
			resultList.retainAll(friendList2);
			return resultList;
			
		} catch(ClassCastException cce) {
			throw new InvalidParamException("ClassCastException");
		}
	}

}
