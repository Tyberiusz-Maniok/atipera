package com.example.demo.repos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepoDto {

    private String name;
    private String owner;
    private List<BranchDto> branches;
}
