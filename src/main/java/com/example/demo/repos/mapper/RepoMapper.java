package com.example.demo.repos.mapper;

import com.example.demo.repos.dto.RepoDto;
import com.example.demo.repos.github.Repo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RepoMapper {

    @Mapping(source = "ownerName", target = "owner")
    RepoDto clientToDto(Repo repo);
}
