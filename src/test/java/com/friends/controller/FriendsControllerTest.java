package com.friends.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.friends.dto.ApiResponse;
import com.friends.exception.GlobalExceptionHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FriendsControllerTest {

	private String baseUrl;
	private String controllerMapping = "/friends";
	private HttpHeaders headers;
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Before
	public void setup() {
		baseUrl = "http://localhost:" + port + controllerMapping;
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void testWithWrongHttpMethodWillFailGracefully() {
		HttpEntity<String> httpEntity = new HttpEntity<String>("", headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/add", HttpMethod.GET, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.METHOD_NOT_ALLOWED);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testAddFriendsSuccessful() throws IOException {
		String json = getJsonContent("add_friends_success.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isTrue();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isNull();
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testAddFriendsWithInvalidJsonRequestWillFail() throws IOException {
		String json = getJsonContent("add_friends_invalid_json.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.UNEXPECTED_MSG);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testAddFriendsWithInvalidValueWillFail() throws IOException {
		String json = getJsonContent("add_friends_invalid_value.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testAddFriendsWithMissingKeyWillFail() throws IOException {
		String json = getJsonContent("add_friends_missing_key.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testListFriendsSuccessfully() throws IOException {
		String json = getJsonContent("populate_common_friend1.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		json = getJsonContent("add_friends_success.json");
		httpEntity = new HttpEntity<String>(json, headers);
		restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		json = getJsonContent("list_friends.json");
		httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/list", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isTrue();
		assertThat(response.getBody().getFriends()).containsExactlyInAnyOrder("john@example.com", "common@example.com");
		assertThat(response.getBody().getCount()).isEqualTo(new Integer(2));
		assertThat(response.getBody().getError()).isNull();
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testListFriendsForNewPersonThrowNotFound() throws IOException {
		String json = getJsonContent("list_friends_not_found.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/list", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.EMAIL_NOT_FOUND);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testListFriendsForMissingKeyWillThrowException() throws IOException {
		String json = getJsonContent("add_friends_success.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/list", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testGetMutualFriendsSuccessful() throws IOException {
		for(int i = 1; i <= 3; i++) {
			String json = getJsonContent("populate_common_friend" + i + ".json");
			HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
			restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		}
		
		String json = getJsonContent("list_mutual_success.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/listMutual", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isTrue();
		assertThat(response.getBody().getFriends()).containsExactlyInAnyOrder("common@example.com");
		assertThat(response.getBody().getCount()).isEqualTo(new Integer(1));
		assertThat(response.getBody().getError()).isNull();
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testListMutualNoCommonFriendsWithEmptyList() throws IOException {
		String json = getJsonContent("list_mutual_no_common.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/listMutual", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isTrue();
		assertThat(response.getBody().getFriends()).isEmpty();;
		assertThat(response.getBody().getCount()).isEqualTo(new Integer(0));
		assertThat(response.getBody().getError()).isNull();
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testListMutualWithInvalidRequestThrowsException() throws IOException {
		String json = getJsonContent("add_friends_missing_key.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/listMutual", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testListMutualWith1EmailNotFoundThrowsException() throws IOException {
		String json = getJsonContent("list_mutual_email_not_found.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/listMutual", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.EMAIL_NOT_FOUND);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testFollowSuccessful() throws IOException {
		String json = getJsonContent("follow.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/follow", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isTrue();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isNull();
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testFollowWithEmptyEmailThrowsException() throws IOException {
		String json = getJsonContent("follow_with_empty_email.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/follow", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();;
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testFollowWithInvalidRequestThrowsException() throws IOException {
		String json = getJsonContent("list_friends.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/follow", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();;
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testBlockSuccessful() throws IOException {
		String json = getJsonContent("block.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/block", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isTrue();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isNull();
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testBlockWithEmptyEmailThrowsException() throws IOException {
		String json = getJsonContent("follow_with_empty_email.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/block", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();;
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testBlockWithInvalidRequestThrowsException() throws IOException {
		String json = getJsonContent("list_friends.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/block", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();;
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testAddFriendWhoHasBeenBlockedWillReturnSuccessFalse() throws IOException {
		String json = getJsonContent("add_friends_after_being_blocked.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isFalse();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isNull();
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testFindAllAllowedUpdates() throws IOException {
		String json = getJsonContent("populate_common_friend1.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		json = getJsonContent("block.json");
		httpEntity = new HttpEntity<String>(json, headers);
		restTemplate.exchange(baseUrl + "/block", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		json = getJsonContent("find_updates.json");
		httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/listUpdatesAllowedFriend", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isTrue();
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isNull();
		assertThat(response.getBody().getRecipients()).containsExactlyInAnyOrder("andy@example.com");
	}
	
	@Test
	public void testFindAllowedWithOneEmptyFieldWillThrowsException() throws IOException {
		String json = getJsonContent("find_updates_with_empty.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/listUpdatesAllowedFriend", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();;
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testFindAllowedWithInvalidRequestThrowsException() throws IOException {
		String json = getJsonContent("list_friends.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/listUpdatesAllowedFriend", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();;
		assertThat(response.getBody().getFriends()).isNullOrEmpty();
		assertThat(response.getBody().getCount()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
		assertThat(response.getBody().getRecipients()).isNullOrEmpty();
	}
	
	@Test
	public void testDefaultMethod() {
		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
		assertThat(response.getBody()).isEqualTo("Hello");
	}
	
	private String getJsonContent(String jsonFile) throws IOException {
		return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(jsonFile), "UTF-8");
	}
}
