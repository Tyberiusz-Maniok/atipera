package com.example.demo.repos.dto;

import lombok.Data;

import java.util.List;

@Data
public class RepoDto {

    private String name;
    private String owner;
    private List<BranchDto> branches;
}
