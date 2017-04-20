package com.friends.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.friends.dao.FriendsDAO;
import com.friends.exception.EmailNotFoundException;
import com.friends.exception.InvalidParamException;

@RunWith(MockitoJUnitRunner.class)
public class FriendsServiceImplTest {
	
	@InjectMocks
	private FriendsServiceImpl unit;
	
	@Mock
	private FriendsDAO mockFriendsDAO;
	
	private List<String> friends;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		friends = new ArrayList<>();
	}

	@Test
	public void testAddFriendsSuccessfully() throws InvalidParamException {
		String friend1 = "kingkong@zoo", friend2 = "panda@zoo";
		friends.add(friend1);
		friends.add(friend2);
		boolean result = unit.addFriend(friends);
		
		assertThat(result).isTrue();
		verify(mockFriendsDAO).addFriend(friend1, friend2);
	}
	
	@Test
	public void testAddFriendsWithSameNameWillNotSucceed() throws InvalidParamException {
		String friend1 = "kingkong@zoo", friend2 = "kingkong@zoo";
		friends.add(friend1);
		friends.add(friend2);
		boolean result = unit.addFriend(friends);
		
		assertThat(result).isFalse();
		verifyZeroInteractions(mockFriendsDAO);
	}
	
	@Test
	public void testAddFriendWithFriendListOverOrLessThan2WillThrowException() {
		try {
			unit.addFriend(friends);
			fail("Should throw exception when less than 2 friend in List");
		} catch (InvalidParamException e) {
			verifyZeroInteractions(mockFriendsDAO);
		}
		
		try {
			friends.add("1");
			friends.add("2");
			friends.add("3");
			unit.addFriend(friends);
			fail("Should throw exception when less than 2 friend in List");
		} catch (InvalidParamException e) {
			verifyZeroInteractions(mockFriendsDAO);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testAddFriendWithWrongTypeWillThrowException() {
		try {
			List mixedList = new ArrayList();
			mixedList.add("kingkong@zoo");
			mixedList.add(new Integer(1));
			
			unit.addFriend(mixedList);
			fail("Should throw exception when List contain wrong type");
		} catch (InvalidParamException ipe) {
			assertThat(ipe.getMessage()).isEqualTo("ClassCastException");
			verifyZeroInteractions(mockFriendsDAO);
		}
	}
	
	@Test
	public void testGetFriendListSuccess() throws InvalidParamException, EmailNotFoundException {
		String friend1 = "kingkong@zoo";
		unit.getFriendList(friend1);
		verify(mockFriendsDAO).getFriendList(friend1);
	}
	
	@Test
	public void testGetFriendListWithEmptyStringWillFail() throws EmailNotFoundException {
		try {
			unit.getFriendList("");
			fail("Should fail if email is empty");
		} catch (InvalidParamException ipe) {
			assertThat(ipe.getMessage()).isEqualTo("String is blank");
			verifyZeroInteractions(mockFriendsDAO);
		}
		
	}
}
