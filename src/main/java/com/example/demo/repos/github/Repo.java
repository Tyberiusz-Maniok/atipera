package com.example.demo.repos.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Repo {

    @JsonProperty("name")
    private String name;

    private String ownerName;
    @JsonProperty("owner")
    private void unpackOwner(Map<String, Object> owner) {
        ownerName = (String) owner.get("login");
    }

    @JsonProperty("fork")
    private boolean fork;

    private List<Branch> branches;
}
