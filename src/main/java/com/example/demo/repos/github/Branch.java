package com.example.demo.repos.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Branch {

    @JsonProperty("name")
    private String name;

    private String lastCommitSha;

    @JsonProperty("commit")
    private void unpackCommit(Map<String, Object> commit) {
        lastCommitSha = (String) commit.get("sha");
    }
}
