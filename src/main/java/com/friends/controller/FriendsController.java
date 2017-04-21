package com.friends.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.friends.dto.ApiResponse;
import com.friends.exception.EmailNotFoundException;
import com.friends.exception.GlobalExceptionHandler;
import com.friends.exception.InvalidParamException;
import com.friends.service.FriendsService;
import com.friends.util.RequestKeys;

@RestController
@RequestMapping("friends")
public class FriendsController {
	
	@Autowired
	protected FriendsService friendsService;
	
	@RequestMapping(value="/add", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	public ApiResponse addFriendConnection(@RequestBody Map<String, Object> requestObj) throws InvalidParamException {
		List<String> friendsParam = getFriendsFromRequest(requestObj);

		ApiResponse result = new ApiResponse();
		result.setSuccess(friendsService.addFriend(friendsParam));
		return result;
	}
	
	@RequestMapping(value="/list", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	public ApiResponse listFriends(@RequestBody Map<String, Object> requestObj) throws InvalidParamException, EmailNotFoundException {
		String email = getStringFromRequest(requestObj, RequestKeys.email);
		
		List<String> friendList = friendsService.getFriendList(email);
		ApiResponse result = new ApiResponse();
		result.setSuccess(true);
		result.setFriends(friendList);
		result.setCount(friendList.size());
		return result;
	}
	
	@RequestMapping(value="/listMutual", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	public ApiResponse listMutualFriends(@RequestBody Map<String, Object> requestObj) throws InvalidParamException, EmailNotFoundException {
		List<String> friendsParam = getFriendsFromRequest(requestObj);
		
		List<String> friendList = friendsService.getMutualFriendList(friendsParam);
		ApiResponse result = new ApiResponse();
		result.setSuccess(true);
		result.setFriends(friendList);
		result.setCount(friendList.size());
		return result;
	}
	
	@RequestMapping(value="/follow", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	public ApiResponse follow(@RequestBody Map<String, Object> requestObj) throws InvalidParamException {
		String requestor = getStringFromRequest(requestObj, RequestKeys.requestor);
		String target = getStringFromRequest(requestObj, RequestKeys.target);
		
		ApiResponse result = new ApiResponse();
		result.setSuccess(friendsService.follow(requestor, target));
		return result;
	}
	
	@RequestMapping(value="/block", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	public ApiResponse block(@RequestBody Map<String, Object> requestObj) throws InvalidParamException {
		String requestor = getStringFromRequest(requestObj, RequestKeys.requestor);
		String target = getStringFromRequest(requestObj, RequestKeys.target);
		
		ApiResponse result = new ApiResponse();
		result.setSuccess(friendsService.block(requestor, target));
		return result;
	}
	
	@RequestMapping(value="/listUpdatesAllowedFriend", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	public ApiResponse listUpdatesAllowedFriend(@RequestBody Map<String, Object> requestObj) throws InvalidParamException {
		String sender = getStringFromRequest(requestObj, RequestKeys.sender);
		String text = getStringFromRequest(requestObj, RequestKeys.text);
		
		ApiResponse result = new ApiResponse();
		result.setSuccess(true);
		result.setRecipients(friendsService.findAllAllowedFriends(sender, text));
		return result;
	}

	@RequestMapping()
	public String defaultMethod(){
		return "Hello";
	}
	
	/*
	 * Helper methods
	 */
	private String getStringFromRequest(Map<String, Object> requestObj, RequestKeys key) throws InvalidParamException {
		if (requestObj.containsKey(key.name()) && requestObj.get(key.name()) instanceof String) {
			return (String) requestObj.get(key.name());
		}
		throw new InvalidParamException(); 
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> getFriendsFromRequest(Map<String, Object> requestObj) throws InvalidParamException {
		if (requestObj.containsKey(RequestKeys.friends.name()) && requestObj.get(RequestKeys.friends.name()) instanceof List) {
			return (List) requestObj.get(RequestKeys.friends.name());
		}
		throw new InvalidParamException(); 
	}
	
	/*
	 * Exception Handling
	 */
	@ExceptionHandler(EmailNotFoundException.class)
	public ApiResponse handleCheckedError(EmailNotFoundException enfe) {
		ApiResponse errorResponse = new ApiResponse();
		errorResponse.setError(GlobalExceptionHandler.EMAIL_NOT_FOUND);
		return errorResponse;
	}

	@ExceptionHandler(InvalidParamException.class)
	public ApiResponse handleCheckedError(InvalidParamException ipe) {
		ApiResponse errorResponse = new ApiResponse();
		errorResponse.setError(GlobalExceptionHandler.INVALID_PARAM_MSG);
		return errorResponse;
	}
	
	@ExceptionHandler(Exception.class)
	public ApiResponse handleError(Exception ex) {
		ApiResponse errorResponse = new ApiResponse();
		errorResponse.setError(GlobalExceptionHandler.UNEXPECTED_MSG);
		return errorResponse;
	}
	
}
