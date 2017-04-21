# friends-management

##### Options for starting the server
Either clone the project, open in IDE and run the java file **FriendsApplication.java** or download the jar in target directory and start it by running 
```
java -jar target/friends-management-0.0.1-SNAPSHOT.jar
```

All APIs only receives HTTP POST, consumes and serve Content-Type of application/json, including error response. The server runs at a default url of **http://localhost:8080**

##### List of api
* an API to create a friend connection between two email addresses 
  - friends/add
```
{
  "friends":
    [
      "andy@example.com",
      "john@example.com"
    ]
}
```

* an API to retrieve the friends list for an email address
  - friends/list
```
{
  "email": "andy@example.com"
}
```

* an API to retrieve the common friends list between two email addresses
  - friends/listMutual
```
{
  "friends":
    [
      "andy@example.com",
      "john@example.com"
    ]
}
```

* an API to subscribe to updates for an email address
  - friends/follow
```
{
  "requestor": "lisa@example.com",
  "target": "john@example.com"
}
```

* an API to block updates for an email address
  - friends/block
```
{
  "requestor": "andy@example.com",
  "target": "john@example.com"
}
```

* an API to retrieve all email addresses that can receive updates for an email address
  - friends/listUpdatesAllowedFriend
```
{
  "sender":  "john@example.com",
  "text": "Hello World! kate@example.com"
}
```

Sample url **http://localhost:8080/friends/add**

All error response follow a simple fixed structur of an error key and its' associated message.
Example:
```
{
    "error": "Missing required parameter(s) or invalid value(s) specified."
}
```

##### Technical details
Api server is written in Java, making use of Spring Boot, as Spring Boot has a build in embedded tomcat server and Spring MVC provides a number of helpful features like JSON parsing, dependency injection.