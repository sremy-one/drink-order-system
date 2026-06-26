package com.drinkorder.mapper;

import com.drinkorder.entity.Branch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BranchMapper {
    Branch findById(Long id);
    List<Branch> findAll();
    List<Branch> findByMerchantId(@Param("merchantId") Long merchantId);
    List<Branch> findOpen();
    void insert(Branch branch);
    void update(Branch branch);
    void delete(Long id);
}
