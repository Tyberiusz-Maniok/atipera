package com.example.demo.repos.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Branch {

    @JsonProperty("name")
    private String name;

    private String lastCommitSha;

    @JsonProperty("commit")
    private void unpackCommit(Map<String, Object> commit) {
        lastCommitSha = (String) commit.get("sha");
    }
}
