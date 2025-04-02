package com.example.demo.repos;

import com.example.demo.repos.dto.RepoDto;
import com.example.demo.repos.github.Branch;
import com.example.demo.repos.github.Repo;
import com.example.demo.repos.mapper.RepoMapper;
import com.example.demo.repos.restclient.GithubApiErrorHandler;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
public class RepoServiceImpl implements RepoService {

    @Value("${github.api}")
    private String repoApi;

    private final RestTemplate restTemplate;

    private final RepoMapper repoMapper = Mappers.getMapper(RepoMapper.class);

    @Autowired
    public RepoServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<RepoDto> getRepos(String username) {
        Repo[] response = restTemplate.getForObject(
                UriComponentsBuilder.fromUriString(repoApi).path("users/{username}/repos").build(username),
                Repo[].class
        );
        List<Repo> repos = Arrays.stream(response).filter(x -> !x.isFork()).toList();
        for (Repo r : repos) {
            Branch[] branchesResponse = restTemplate.getForObject(
                    UriComponentsBuilder.fromUriString(repoApi).path("repos/{username}/{repo}/branches").build(r.getOwnerName(), r.getName()),
                    Branch[].class
            );
            r.setBranches(Arrays.asList(branchesResponse));
        }
        return  repos.stream().map(repoMapper::clientToDto).toList();
    }
}
