package com.example.demo.repos;

import com.example.demo.repos.dto.RepoDto;
import com.example.demo.repos.github.Branch;
import com.example.demo.repos.github.Repo;
import com.example.demo.repos.mapper.RepoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

@Service
public class RepoService {

    @Value("${github.url}")
    private String repoBaseUrl;

    private RestTemplate restTemplate = new RestTemplate();

    private RepoMapper repoMapper = Mappers.getMapper(RepoMapper.class);

    public List<RepoDto> getRepos(String username) {
        ResponseEntity<Repo[]> response = restTemplate.getForEntity(
                UriComponentsBuilder.fromUriString(repoBaseUrl).path("users/{username}").build(username),
                Repo[].class
        );
        if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
            List<Repo> repos = Arrays.stream(response.getBody()).filter(Repo::isFork).toList();
            for (Repo r : repos) {
                ResponseEntity<Branch[]> branchesResponse = restTemplate.getForEntity(
                        UriComponentsBuilder.fromUriString(r.getBranchesUrl()).build().toUri(),
                        Branch[].class
                );
                r.setBranches(Arrays.asList(branchesResponse.getBody()));
            }
            return repos.stream().map(x -> repoMapper.clientToDto(x)).toList();
        } else if (response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("User {0} was not found", username));
        } else {
            throw new RuntimeException("Encountered status code that is not handled");
        }
    }
}
