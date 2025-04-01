package com.example.demo.repos.mapper;

import com.example.demo.repos.dto.BranchDto;
import com.example.demo.repos.dto.RepoDto;
import com.example.demo.repos.github.Repo;
import org.mapstruct.Mapper;

@Mapper
public interface RepoMapper {

    RepoDto clientToDto(Repo repo);
}
