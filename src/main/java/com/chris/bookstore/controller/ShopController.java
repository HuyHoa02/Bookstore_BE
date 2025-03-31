package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.AddressRequest;
import com.chris.bookstore.dto.request.BookRequest;
import com.chris.bookstore.dto.response.AddressResponse;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.BookCreationResponse;
import com.chris.bookstore.service.AddressService;
import com.chris.bookstore.service.BookService;
import com.chris.bookstore.service.CategoryService;
import com.chris.bookstore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/shops")
public class ShopController {




}
