package com.example.demo.repos;

import com.example.demo.repos.dto.RepoDto;

import java.util.List;

public interface RepoService {
    List<RepoDto> getRepos(String username);
}
