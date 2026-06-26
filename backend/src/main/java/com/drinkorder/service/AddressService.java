package com.drinkorder.service;

import com.drinkorder.entity.Address;
import com.drinkorder.mapper.AddressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    private final AddressMapper addressMapper;

    public AddressService(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    public List<Address> findByUserId(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    public Address findById(Long id) {
        return addressMapper.findById(id);
    }

    public Address save(Address address) {
        if (address.getId() == null) {
            addressMapper.insert(address);
        } else {
            addressMapper.update(address);
        }
        return address;
    }

    public void delete(Long id) {
        addressMapper.delete(id);
    }
}
