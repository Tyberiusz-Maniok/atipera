package com.example.demo.repos.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Repo {

    @JsonProperty("name")
    private String name;

    private String ownerName;
    @JsonProperty("owner")
    private void unpackOwner(Map<String, Object> owner) {
        ownerName = (String) owner.get("name");
    }

    @JsonProperty("fork")
    private boolean fork;

    @JsonProperty("branches_url")
    private String branchesUrl;
}
