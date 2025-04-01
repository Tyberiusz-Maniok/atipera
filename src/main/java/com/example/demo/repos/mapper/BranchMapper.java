package com.example.demo.repos.mapper;

import com.example.demo.repos.dto.BranchDto;
import com.example.demo.repos.github.Branch;
import org.mapstruct.Mapper;

@Mapper
public interface BranchMapper {

    BranchDto clientToDto(Branch branch);
}
