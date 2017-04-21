package com.friends.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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
	public void testAddFriendsResultFollowDAOResultIfPassValidation() throws InvalidParamException {
		String friend1 = "kingkong@zoo", friend2 = "panda@zoo";
		friends.add(friend1);
		friends.add(friend2);
		
		boolean daoResult = true;
		
		when(mockFriendsDAO.addFriend(friend1, friend2)).thenReturn(daoResult);
		
		boolean result = unit.addFriend(friends);
		
		assertThat(result).isEqualTo(daoResult);
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
		} finally {
			verifyZeroInteractions(mockFriendsDAO);
		}
		
		try {
			friends.add("1");
			friends.add("2");
			friends.add("3");
			unit.addFriend(friends);
			fail("Should throw exception when more than 2 friend in List");
		} catch (InvalidParamException e) {
		} finally {
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
		} finally {
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
		} finally {
			verifyZeroInteractions(mockFriendsDAO);
		}
	}
	
	@Test
	public void testGetMutualFriendsSuccess() throws InvalidParamException, EmailNotFoundException {
		String friend1 = "kingkong@zoo", friend2 = "panda@zoo";
		friends.add(friend1);
		friends.add(friend2);
		
		List<String> friend1List = new ArrayList<>(Arrays.asList("tiger@zoo", "monkey@zoo", "zebra@zoo"));
		List<String> friend2List = new ArrayList<>(Arrays.asList("lion@zoo", "zebra@zoo", "deer@zoo", "tiger@zoo"));
		
		when(mockFriendsDAO.getFriendList(friend1)).thenReturn(friend1List);
		when(mockFriendsDAO.getFriendList(friend2)).thenReturn(friend2List);
		
		List<String> mutualList = unit.getMutualFriendList(friends);
		
		assertThat(mutualList).containsExactlyInAnyOrder("tiger@zoo", "zebra@zoo");
	}
	
	@Test
	public void testGetMutualFriendsIfEmailDontExistThrowNotFound() throws InvalidParamException {
		try {
			String friend1 = "kingkong@zoo", notFound = "cantFindMe@anywhere";
			friends.add(friend1);
			friends.add(notFound);
			
			List<String> friend1List = new ArrayList<>(Arrays.asList("tiger@zoo", "monkey@zoo", "zebra@zoo"));
			
			when(mockFriendsDAO.getFriendList(friend1)).thenReturn(friend1List);
			when(mockFriendsDAO.getFriendList(notFound)).thenThrow(new EmailNotFoundException());
		
			unit.getMutualFriendList(friends);
			fail("Should have thrown EmailNotFoundException");
		} catch (EmailNotFoundException e) {}
	}
	
	@Test
	public void testGetMutualFriendListOverOrLessThan2WillThrowException() throws EmailNotFoundException {
		try {
			unit.getMutualFriendList(friends);
			fail("Should throw exception when less than 2 friend in List");
		} catch (InvalidParamException e) {
		} finally {
			verifyZeroInteractions(mockFriendsDAO);
		}
		
		try {
			friends.add("1");
			friends.add("2");
			friends.add("3");
			unit.getMutualFriendList(friends);
			fail("Should throw exception when more than 2 friend in List");
		} catch (InvalidParamException e) {
		} finally {
			verifyZeroInteractions(mockFriendsDAO);
		}
	}
	
	@Test
	public void testFollowSuccessful() throws InvalidParamException {
		String requestor = "kids@zoo", target = "kingkong@zoo";
		unit.follow(requestor, target);
		verify(mockFriendsDAO).follow(requestor, target);
	}
	
	@Test
	public void testFollowIfOneEmailIsEmptyThrowsException() {
		try {
			String requestor = "kids@zoo", target = "";
			unit.follow(requestor, target);
			fail("Should have fail if one email is empty");
		} catch (InvalidParamException ipe) {
			assertThat(ipe.getMessage()).isEqualTo("String is blank");
		} finally {
			verifyZeroInteractions(mockFriendsDAO);
		}
	}
	
	@Test
	public void testBlockSuccessful() throws InvalidParamException {
		String requestor = "kids@zoo", target = "kingkong@zoo";
		unit.block(requestor, target);
		verify(mockFriendsDAO).block(requestor, target);
	}
	
	@Test
	public void testBlockIfOneEmailIsEmptyThrowsException() {
		try {
			String requestor = "kids@zoo", target = "";
			unit.block(requestor, target);
			fail("Should have fail if one email is empty");
		} catch (InvalidParamException ipe) {
			assertThat(ipe.getMessage()).isEqualTo("String is blank");
		} finally {
			verifyZeroInteractions(mockFriendsDAO);
		}
	}
	
	@Test
	public void testFindAllowdedSuccessful() throws InvalidParamException {
		String sender = "kids@zoo", text = "king kong zoo";
		List<String> splitText = new ArrayList<>(Arrays.asList("king", "kong", "zoo"));
		unit.findAllAllowedFriends(sender, text);
		verify(mockFriendsDAO).findAllAllowedFriends(sender, splitText);
	}
	
	@Test
	public void testFindAllowedIfOneEmailIsEmptyThrowsException() {
		try {
			String sender = "kids@zoo", text = "";
			unit.findAllAllowedFriends(sender, text);
			fail("Should have fail if one of the field is empty");
		} catch (InvalidParamException ipe) {
			assertThat(ipe.getMessage()).isEqualTo("String is blank");
		} finally {
			verifyZeroInteractions(mockFriendsDAO);
		}
	}
	
}
