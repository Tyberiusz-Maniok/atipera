package com.example.demo.repos.dto;

import lombok.Data;

@Data
public class BranchDto {

    private String name;
    private String lastCommitSha;
}
