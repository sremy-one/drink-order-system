package com.drinkorder.service;

import com.drinkorder.entity.Branch;
import com.drinkorder.mapper.BranchMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BranchService {
    private final BranchMapper branchMapper;

    public BranchService(BranchMapper branchMapper) {
        this.branchMapper = branchMapper;
    }

    public List<Branch> findAll() {
        return branchMapper.findAll();
    }

    public Branch findById(Long id) {
        return branchMapper.findById(id);
    }

    @Transactional
    public Branch save(Branch branch) {
        if (branch.getId() == null) {
            branchMapper.insert(branch);
            return branchMapper.findById(branch.getId());
        } else {
            branchMapper.update(branch);
            return branchMapper.findById(branch.getId());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        branchMapper.delete(id);
    }

    public List<Branch> findByMerchantId(Long merchantId) {
        return branchMapper.findByMerchantId(merchantId);
    }
}
