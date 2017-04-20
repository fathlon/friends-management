package com.friends.dao;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import com.friends.model.Friend;

@RunWith(MockitoJUnitRunner.class)
public class FriendsDAOImplTest {
	
	@InjectMocks
	private FriendsDAOImpl unit;
	
	@Mock
	private Map<String, Friend> mockFriendsDB; 

	@Before
	public void setup() {
		Whitebox.setInternalState(unit, "friendsDB", mockFriendsDB);
	}
	
	@Test
	public void addFriendSuccess() {
		String friendEmail1 = "kingkong@zoo", friendEmail2 = "panda@zoo";
		Friend mockFriend1 = mock(Friend.class);
		Friend mockFriend2 = mock(Friend.class);
		
		when(mockFriend1.getEmail()).thenReturn(friendEmail1);
		when(mockFriend2.getEmail()).thenReturn(friendEmail2);
		
		when(mockFriendsDB.getOrDefault(eq(friendEmail1), any(Friend.class))).thenReturn(mockFriend1);
		when(mockFriendsDB.getOrDefault(eq(friendEmail2), any(Friend.class))).thenReturn(mockFriend2);
		
		unit.addFriend(friendEmail1, friendEmail2);
		
		verify(mockFriend1).addFriend(mockFriend2);
		verify(mockFriend2).addFriend(mockFriend1);
		
		verify(mockFriendsDB).put(friendEmail1, mockFriend1);
		verify(mockFriendsDB).put(friendEmail2, mockFriend2);
		
	}
}
