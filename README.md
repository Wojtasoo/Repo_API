# GitHub Repositories API

A Spring Boot REST API application built with Java 21 that lists non-fork GitHub repositories for a given user. For each repository, it also returns branch information including the branch name and the last commit SHA. The API handles errors gracefully, including returning robust error messages with details provided by the GitHub API.

## Table of Contents

- [Tech Stack](#tech-stack)
- [Dependencies](#dependencies)
- [Features](#features)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Additional Information](#additional-information)

## Tech Stack

- **Java 21**
- **Spring Boot 3.1.2**
- **Spring MVC (Spring Web)**
- **Spring Boot Actuator** (for shutdown endpoint)
- **Gradle (Kotlin DSL)**
- **JUnit 5** (for testing)
- **MockRestServiceServer** (for integration tests)
- **Jackson** (for JSON serialization)

## Dependencies

The project uses the following key dependencies:

- `org.springframework.boot:spring-boot-starter`  
  Base Spring Boot dependency.
- `org.springframework.boot:spring-boot-starter-web`  
  To build RESTful web services.
- `org.springframework.boot:spring-boot-starter-actuator`  
  To expose management endpoints, including the shutdown endpoint.
- `org.springframework.boot:spring-boot-starter-test`  
  For testing support including JUnit 5 and Spring testing utilities.
- `com.fasterxml.jackson.core:jackson-databind`  
  For JSON serialization/deserialization.

The Gradle build file (using Kotlin DSL) includes these dependencies and sets the Java source compatibility to Java 21.

## Features

- **List Non-Fork Repositories:**  
  Retrieves GitHub repositories for a given user and filters out forked repositories.
- **Branch Details:**  
  Returns the branch name and the last commit SHA for each repository.
- **Error Handling:**  
  Provides robust error messages. If GitHub returns an error (e.g., rate limit exceeded), the API responds with the full GitHub error message.
- **User Not Found:**  
  Returns a 404 error with a message when a non-existent GitHub user is requested.
- **Input Validation:**  
  Returns a 400 error for invalid inputs (e.g., empty username).
- **Graceful Shutdown:**  
  Exposes a shutdown endpoint (via Spring Boot Actuator) for controlled termination of the application.

## Running the Application

1. **Clone the Repository**

   ```bash
   git clone https://github.com/your-username/github-repositories-api.git
   cd github-repositories-api
   ```

2. **Build the Application**
   
  This project uses **Gradle** as its build tool.
  To build the application, run:
  
  ```bash
  ./gradlew build
  ```
  This will compile the source code, run tests, and generate a JAR file in the `build/libs` directory.

## Running the Application

After building the application, you can run it in two main ways:

### Option 1: Using Gradle

```bash
./gradlew bootRun
```
For Windows users:

```bash
gradlew.bat bootRun
```

### Option 2: Running the JAR File

After building the application, you can run the generated JAR file using the following steps:

1. Navigate to the `build/libs` directory.
2. Run the application with:

  ```bash
  java -jar github-repositories-api-0.0.1-SNAPSHOT.jar
  ```
  Once started, the application will be available at:
  ```arduino
  http://localhost:8080
  ```

## API Endpoint

**GET** `/users/{username}/repositories`

Fetches non-fork repositories with their branches and last commit SHA.

### Example

```bash
curl http://localhost:8080/users/octocat/repositories
```

**Shutdown Endpoint** `/actuator/shutdown`

I used a SpringBoot shutdown endpoint to gracefully stop the application.

### Example
```bash
curl -X POST http://localhost:8080/actuator/shutdown
```

## Testing

You can run the test suite using the Gradle wrapper.

### Run Tests

```bash
./gradlew test
```

This command will execute all unit and integration tests in the project.

## Additional Information

### API Rate Limiting

When calling the GitHub API, keep in mind that unauthenticated requests are subject to strict rate limits.

### Error Handling

The application includes enhanced error messages to help diagnose issues with external API requests. For instance, if a call to GitHub fails, the response body from GitHub will be logged and included in the exception message for easier debugging.

### Common Misuse Scenarios

Here are a few potential user errors and how the application handles them:

- **Missing Parameters**: If required query parameters are omitted, the API will return a `400 Bad Request` with a clear message.

- **Malformed Requests**: Invalid JSON or unexpected input will be handled gracefully with helpful error descriptions.

- **Rate Limiting**: As noted above, rate-limited responses from GitHub are caught and wrapped in custom exceptions with detailed messages.

- **Invalid Usernames**: If the GitHub username does not exist or is misspelled, the user will receive a `404 Not Found` with context.

- **Unreachable External Services**: In case of downtime or network issues, the application returns a `503 Service Unavailable`.
