package com.github;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class Controller {
    private final APIService apiService;

    public Controller(APIService apiService)
    {
        this.apiService=apiService;
    }

    @GetMapping("/{username}/repositories")
    public ResponseEntity<List<APIResponse>> getRepositories(@PathVariable String username) {
        List<APIResponse> repositories = apiService.getRepositories(username);
        return ResponseEntity.ok(repositories);
    }
}
