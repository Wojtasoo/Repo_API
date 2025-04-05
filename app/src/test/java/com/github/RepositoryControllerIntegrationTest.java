package com.github;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RepositoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testGetRepositories_UserFound() throws Exception {
        String username = "testuser";
        String reposResponse = """
                [
                  {"name": "repo1", "fork": false, "owner": {"login": "testuser"}},
                  {"name": "repo2", "fork": true, "owner": {"login": "testuser"}}
                ]""";
        String branchesResponse = """
                [
                  {"name": "main", "commit": {"sha": "abc123"}}
                ]""";

        mockServer.expect(requestTo("https://api.github.com/users/" + username + "/repos"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(reposResponse, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://api.github.com/repos/" + username + "/repo1/branches"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(branchesResponse, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/users/" + username + "/repositories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].repositoryName").value("repo1"))
                .andExpect(jsonPath("$[0].ownerLogin").value("testuser"))
                .andExpect(jsonPath("$[0].branches[0].name").value("main"))
                .andExpect(jsonPath("$[0].branches[0].lastCommitSha").value("abc123"))
                .andExpect(jsonPath("$", hasSize(1)));

        mockServer.verify();
    }

    @Test
    void testGetRepositories_UserNotFound() throws Exception {
        String username = "nonexistinguser";
        mockServer.expect(requestTo("https://api.github.com/users/" + username + "/repos"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/users/" + username + "/repositories"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Given User doesn't exist"));

        mockServer.verify();
    }

    @Test
    public void testRateLimitExceeded() throws Exception {
        String username = "testuser";
        String rateLimitResponse = "{\"message\":\"API rate limit exceeded for 89.64.70.50. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)\",\"documentation_url\":\"https://docs.github.com/rest/overview/resources-in-the-rest-api#rate-limiting\"}";

        mockServer.expect(requestTo("https://api.github.com/users/" + username + "/repos"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .body(rateLimitResponse)
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/users/" + username + "/repositories"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value(rateLimitResponse));

        mockServer.verify();
    }

    @Test
    public void testEmptyUsername() throws Exception {

        mockMvc.perform(get("/users/   /repositories"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Username must not be empty"));
    }
}
