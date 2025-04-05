package com.github;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class APIService {
    private final RestTemplate restTemplate;

    public APIService(RestTemplate restTemplate)
    {
        this.restTemplate=restTemplate;
    }

    public List<APIResponse> getRepositories(String username)
    {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be empty");
        }

        String URL_repos="https://api.github.com/users/{username}/repos";
        ResponseEntity<List<Map<String, Object>>> response;
        try{
            response= restTemplate.exchange(
                    URL_repos,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    }, username);
        }catch (HttpClientErrorException.NotFound err)
        {
            throw new UserNotFoundException("Given User doesn't exist");
        }catch (HttpClientErrorException err) {
            throw new ErrorMessage(err.getStatusCode(), err.getResponseBodyAsString());
        }

        List<Map<String, Object>> repos= response.getBody();
        if (repos==null)
        {
            return Collections.emptyList();
        }

        List<APIResponse> result= new ArrayList<>();
        for(Map<String, Object> repo: repos){
            Boolean isfork=(Boolean) repo.get("fork");
            if (isfork!=null && isfork)
            {
                continue;
            }

            String name_repo=(String) repo.get("name");
            Map<String, Object> owner=(Map<String, Object>) repo.get("owner");
            String login=owner !=null ? (String) owner.get("login") : null;

            String URL_branches= "https://api.github.com/repos/{username}/{repoName}/branches";
            ResponseEntity<List<Map<String, Object>>> response_branch= restTemplate.exchange(
                    URL_branches,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    }, username, name_repo);

            List<Map<String, Object>> branches= response_branch.getBody();

            List<BranchResponse> responses_branches= new ArrayList<>();
            if (branches != null) {
                for (Map<String, Object> branch : branches) {
                    String branchName = (String) branch.get("name");
                    Map<String, Object> commit = (Map<String, Object>) branch.get("commit");
                    String sha = commit != null ? (String) commit.get("sha") : null;
                    responses_branches.add(new BranchResponse(branchName, sha));
                }
            }
            result.add(new APIResponse(name_repo, login, responses_branches));
        }
        return result;
    }
}
