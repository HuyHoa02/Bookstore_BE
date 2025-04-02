package com.chris.bookstore.repository;

import com.chris.bookstore.dto.request.AddressRequest;
import com.chris.bookstore.dto.response.AddressResponse;
import com.chris.bookstore.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByUserIdAndId(Long userId, Long addressId);

    List<Address> findAllByUserId(Long userId);

}
