package com.friends.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

import com.friends.exception.EmailNotFoundException;
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
	public void testAddFriendSuccess() {
		String friendEmail1 = "kingkong@zoo", friendEmail2 = "panda@zoo";
		Friend mockFriend1 = mock(Friend.class);
		Friend mockFriend2 = mock(Friend.class);
		
		when(mockFriend1.getEmail()).thenReturn(friendEmail1);
		when(mockFriend2.getEmail()).thenReturn(friendEmail2);
		
		when(mockFriendsDB.getOrDefault(eq(friendEmail1), any(Friend.class))).thenReturn(mockFriend1);
		when(mockFriendsDB.getOrDefault(eq(friendEmail2), any(Friend.class))).thenReturn(mockFriend2);
		
		unit.addFriend(friendEmail1, friendEmail2);
		
		verify(mockFriend1).addFriend(friendEmail2);
		verify(mockFriend2).addFriend(friendEmail1);
		
		verify(mockFriendsDB).put(friendEmail1, mockFriend1);
		verify(mockFriendsDB).put(friendEmail2, mockFriend2);
	}
	
	@Test
	public void testGetFriendListSuccess() throws EmailNotFoundException {
		String friendEmail1 = "kingkong@zoo";
		Friend mockFriend1 = mock(Friend.class);
		
		when(mockFriendsDB.get(friendEmail1)).thenReturn(mockFriend1);
		
		unit.getFriendList(friendEmail1);
		
		verify(mockFriend1).getFriends();
	}
	
	@Test
	public void testGetFriendListIfEmailNotFoundWillThrowException() {
		try {
			String notFoundEmail = "cantFindMe@anywhere";
			unit.getFriendList(notFoundEmail);
			fail("Should have thrown not found exception");
		} catch (EmailNotFoundException e) {}
	}
	
	@Test
	public void testFollowSuccessful() {
		String req = "markZuck@fb", tar = "commoner@low";
		Friend mockRequestor = mock(Friend.class);
		Friend mockTarget = mock(Friend.class);
		
		when(mockRequestor.getEmail()).thenReturn(req);
		when(mockTarget.getEmail()).thenReturn(tar);
		
		when(mockFriendsDB.getOrDefault(eq(req), any(Friend.class))).thenReturn(mockRequestor);
		when(mockFriendsDB.getOrDefault(eq(tar), any(Friend.class))).thenReturn(mockTarget);
		
		boolean success = unit.follow(req, tar);
		
		verify(mockRequestor).addFollowing(tar);
		verify(mockTarget, never()).addFollowing(anyString());
		
		verify(mockFriendsDB).put(req, mockRequestor);
		verify(mockFriendsDB).put(tar, mockTarget);
		
		assertThat(success).isTrue();
	}
	
}
