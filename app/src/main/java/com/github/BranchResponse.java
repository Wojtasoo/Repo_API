package com.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BranchResponse {
    private String name;

    @JsonProperty("lastCommitSha")
    private String commit_SHA;

    public BranchResponse(String name, String commit_SHA)
    {
        this.name=name;
        this.commit_SHA=commit_SHA;
    }

    public String getName() {
        return name;
    }

    public String getCommit_SHA() {
        return commit_SHA;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCommit_SHA(String commit_SHA) {
        this.commit_SHA = commit_SHA;
    }
}
