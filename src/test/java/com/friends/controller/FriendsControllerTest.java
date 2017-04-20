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
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.METHOD_NOT_ALLOWED);
	}
	
	@Test
	public void testAddFriendsSuccessful() throws IOException {
		String json = getJsonContent("add_friends_success.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isTrue();
		assertThat(response.getBody().getError()).isNullOrEmpty();
	}
	
	@Test
	public void testAddFriendsWithInvalidJsonRequestWillFail() throws IOException {
		String json = getJsonContent("add_friends_invalid_json.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.UNEXPECTED_MSG);
	}
	
	@Test
	public void testAddFriendsWithInvalidValueWillFail() throws IOException {
		String json = getJsonContent("add_friends_invalid_value.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
	}
	
	@Test
	public void testAddFriendsWithMissingKeyWillFail() throws IOException {
		String json = getJsonContent("add_friends_missing_key.json");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/add", HttpMethod.POST, httpEntity, ApiResponse.class);
		
		assertThat(response.getBody().getSuccess()).isNull();
		assertThat(response.getBody().getError()).isEqualTo(GlobalExceptionHandler.INVALID_PARAM_MSG);
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
