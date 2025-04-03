package com.chris.bookstore.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressRequest {
    @NotBlank(message = "Street cannot be empty")
    @Size(max = 100, message = "Street name should not exceed 100 characters")
    private String street;

    @NotBlank(message = "Ward cannot be empty")
    @Size(max = 50, message = "Ward name should not exceed 50 characters")
    private String ward;

    @NotBlank(message = "District cannot be empty")
    @Size(max = 50, message = "District name should not exceed 50 characters")
    private String district;

    @NotBlank(message = "Province cannot be empty")
    @Size(max = 50, message = "Province name should not exceed 50 characters")
    private String province;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
