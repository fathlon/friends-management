package com.friends.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("friends")
public class FriendsController {

	@RequestMapping(value="/add", method=RequestMethod.GET, produces="application/json")
	public String addFriendConnection() {
		return "add";
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
	
	@RequestMapping()
	public String defaultMethod(){
		return "Hello";
	}
}
