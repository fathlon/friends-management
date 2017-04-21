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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
	public void testAddFriendSuccessWhenNotInEitherBlocklist() {
		String friendEmail1 = "kingkong@zoo", friendEmail2 = "panda@zoo";
		Friend mockFriend1 = mock(Friend.class);
		Friend mockFriend2 = mock(Friend.class);
		
		when(mockFriend1.getEmail()).thenReturn(friendEmail1);
		when(mockFriend2.getEmail()).thenReturn(friendEmail2);
		when(mockFriend1.isBlocking(friendEmail2)).thenReturn(false);
		when(mockFriend2.isBlocking(friendEmail1)).thenReturn(false);
		
		when(mockFriendsDB.getOrDefault(eq(friendEmail1), any(Friend.class))).thenReturn(mockFriend1);
		when(mockFriendsDB.getOrDefault(eq(friendEmail2), any(Friend.class))).thenReturn(mockFriend2);
		
		boolean success = unit.addFriend(friendEmail1, friendEmail2);
		
		verify(mockFriend1).addFriend(friendEmail2);
		verify(mockFriend2).addFriend(friendEmail1);
		
		verify(mockFriendsDB).put(friendEmail1, mockFriend1);
		verify(mockFriendsDB).put(friendEmail2, mockFriend2);
		
		assertThat(success).isTrue();
	}
	
	@Test
	public void testAddFriendFailWhenEitherIsBlocking() {
		String friendEmail1 = "kingkong@zoo", friendEmail2 = "panda@zoo";
		Friend mockFriend1 = mock(Friend.class);
		Friend mockFriend2 = mock(Friend.class);
		
		when(mockFriendsDB.getOrDefault(eq(friendEmail1), any(Friend.class))).thenReturn(mockFriend1);
		when(mockFriendsDB.getOrDefault(eq(friendEmail2), any(Friend.class))).thenReturn(mockFriend2);
		
		when(mockFriend1.isBlocking(friendEmail2)).thenReturn(true);
		when(mockFriend2.isBlocking(friendEmail1)).thenReturn(false);
		
		boolean success = unit.addFriend(friendEmail1, friendEmail2);
		
		verify(mockFriend1, never()).addFriend(anyString());
		verify(mockFriend2, never()).addFriend(anyString());
		
		verify(mockFriendsDB, never()).put(anyString(), any(Friend.class));
		verify(mockFriendsDB, never()).put(anyString(), any(Friend.class));
		
		assertThat(success).isFalse();
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
	
	@Test
	public void testBlockSuccessful() {
		String req = "markZuck@fb", tar = "commoner@low";
		Friend mockRequestor = mock(Friend.class);
		Friend mockTarget = mock(Friend.class);
		
		when(mockRequestor.getEmail()).thenReturn(req);
		when(mockTarget.getEmail()).thenReturn(tar);
		
		when(mockFriendsDB.getOrDefault(eq(req), any(Friend.class))).thenReturn(mockRequestor);
		when(mockFriendsDB.getOrDefault(eq(tar), any(Friend.class))).thenReturn(mockTarget);
		
		boolean success = unit.block(req, tar);
		
		verify(mockRequestor).addBlock(tar);
		verify(mockTarget, never()).addBlock(anyString());
		
		verify(mockFriendsDB).put(req, mockRequestor);
		verify(mockFriendsDB).put(tar, mockTarget);
		
		assertThat(success).isTrue();
	}
	
	@Test
	public void testfindAllAllowedFriends() {
		String senderEmail = "spammer@everywhere";
		String story = "In the land far far away, "
				+ "I am walking-pass a stream and I see a woman, i-stalk her and "
				+ "upon noticing me, she says i-dont-really-like-you i-like-spammer . ";
		
		List<String> splitText = new ArrayList<>(Arrays.asList(story.split("\\s")));
		
		Friend spammerFriend = new Friend("i-like-spammer");
		spammerFriend.addFriend(senderEmail);
		
		Friend notFriendButFollow = new Friend("i-stalk");
		notFriendButFollow.addFollowing(senderEmail);
		
		Friend friendButBlockingSpammer = new Friend("i-dont-really-like-you");
		friendButBlockingSpammer.addFriend(senderEmail);
		friendButBlockingSpammer.addBlock(senderEmail);
		
		Friend randomGuy = new Friend("walking-pass");
		
		Friend farawayGuy = new Friend("faraway");
		farawayGuy.addFriend(spammerFriend.getEmail());
		farawayGuy.addFriend(notFriendButFollow.getEmail());
		
		Map<String, Friend> testFriendsDB = new HashMap<>();
		testFriendsDB.put(spammerFriend.getEmail(), spammerFriend);
		testFriendsDB.put(notFriendButFollow.getEmail(), notFriendButFollow);
		testFriendsDB.put(friendButBlockingSpammer.getEmail(), friendButBlockingSpammer);
		testFriendsDB.put(randomGuy.getEmail(), randomGuy);
		testFriendsDB.put(farawayGuy.getEmail(), farawayGuy);
		
		Whitebox.setInternalState(unit, "friendsDB", testFriendsDB);
		
		List<String> result = unit.findAllAllowedFriends(senderEmail, splitText);
		
		assertThat(result).containsExactlyInAnyOrder(spammerFriend.getEmail(), notFriendButFollow.getEmail(), randomGuy.getEmail());
	}
	
}
