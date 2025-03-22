package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.AddressRequest;
import com.chris.bookstore.dto.response.AddressResponse;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService)
    {
        this.addressService = addressService;
    }

    @GetMapping("userId")
    public ApiResponse<List<AddressResponse>> getAllAddresses(@PathVariable(value = "userId") Long userId)
    {
        List<AddressResponse> list = this.addressService.getAll(userId);

        ApiResponse<List<AddressResponse>> res = new ApiResponse<List<AddressResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Getting all addresses succeed");
        res.setResult(list);

        return res;
    }

    @PostMapping
    public ApiResponse<Void> addAddress(@Valid @RequestBody AddressRequest request)
    {
        this.addressService.addAddress(request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.CREATED.value());
        res.setMessage("Getting all addresses succeed");

        return res;
    }

    @PutMapping("{addressId}")
    public ApiResponse<Void> updateAddress(@PathVariable(value = "addressId") Long addressId,
                                           @Valid @RequestBody AddressRequest request)
    {
        this.addressService.updateAddress(addressId, request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Updating all addresses succeed");

        return res;
    }

    @DeleteMapping("{addressId}")
    public ApiResponse<Void> updateAddress(@PathVariable(value = "addressId") Long addressId)
    {
        this.addressService.deleteAddress(addressId);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Deleting all addresses succeed");

        return res;
    }
}
