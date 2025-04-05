package com.github;

import java.util.List;

public class APIResponse {
    private String repositoryName;
    private String ownerLogin;
    private List<BranchResponse> branches;

    public APIResponse(String repositoryName, String ownerLogin, List<BranchResponse> branches){
        this.repositoryName=repositoryName;
        this.ownerLogin=ownerLogin;
        this.branches=branches;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public List<BranchResponse> getBranches() {
        return branches;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public void setBranches(List<BranchResponse> branches) {
        this.branches = branches;
    }
}
