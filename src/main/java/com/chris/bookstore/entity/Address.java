package com.chris.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;      // e.g., "130 Đường Lê Lợi"
    private String ward;        // e.g., "Phường Bến Nghé"
    private String district;    // e.g., "Quận 1"
    private String province;    // e.g., "TP. Hồ Chí Minh"

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getAddresses().contains(this)) {
            user.getAddresses().add(this);
        }
    }

    public Address(String street, String ward, String district, String province, User user) {
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.province = province;
        this.user = user;
    }
    public Address() {
    }

    @Override
    public String toString() {
        return street + ", " + ward + ", " + district + ", " + province;
    }
}
