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
	
	@RequestMapping()
	public String defaultMethod(){
		return "Hello";
	}
	
	/*
	 * Exception Handling
	 */

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
	
	/*
	 * Helper methods
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> getFriendsFromRequest(Map<String, Object> requestObj) throws InvalidParamException {
		if (requestObj.containsKey(RequestKeys.friends.name()) && requestObj.get(RequestKeys.friends.name()) instanceof List) {
			return (List) requestObj.get(RequestKeys.friends.name());
		}
		throw new InvalidParamException(); 
	}
	
	
//	@RequestMapping(value="/list", method=RequestMethod.GET, produces="application/json")
//	public String listFriends() {
//		return "list";
//	}
//	
//	@RequestMapping(value="/listMutual", method=RequestMethod.GET, produces="application/json")
//	public String listMutualFriends() {
//		return "listMutual";
//	}
//	
//	@RequestMapping(value="/follow", method=RequestMethod.GET, produces="application/json")
//	public String follow() {
//		return "follow";
//	}
//
//	@RequestMapping(value="/block", method=RequestMethod.GET, produces="application/json")
//	public String block() {
//		return "block";
//	}
//
//	@RequestMapping(value="/listAllWithUpdatesEnabled", method=RequestMethod.GET, produces="application/json")
//	public String listAllWithUpdatesEnabled() {
//		return "listAllWithUpdatesEnabled";
//	}
	
}
