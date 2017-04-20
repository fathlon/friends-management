package com.friends.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FriendsControllerTest {

	private String baseUrl;
	private String controllerMapping = "/friends";
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Before
	public void setup() {
		this.baseUrl = "http://localhost:" + port + controllerMapping;
	}
	
	@Test
	public void testAddFriends() {
		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/add", String.class);
		assertThat(response.getBody()).isEqualTo("add");
	}
	
	@Test
	public void testDefaultMethod() {
		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
		assertThat(response.getBody()).isEqualTo("Hello");
	}
}
