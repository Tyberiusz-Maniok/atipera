package com.example.demo.repos;

import com.example.demo.repos.dto.ErrorStatusDto;
import com.example.demo.repos.dto.RepoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class RepoController {

    private final RepoService repoService;

    @Autowired
    public RepoController(RepoService repoService) {
        this.repoService = repoService;
    }

    @GetMapping("/{username}")
    public List<RepoDto> getRepos(@PathVariable String username) {
        return repoService.getRepos(username);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResponseStatusException.class)
    public ErrorStatusDto handle404(ResponseStatusException e) {
        return ErrorStatusDto.fromStatusException(e);
    }

}
