package com.example.doanbe.services;

import com.example.doanbe.payload.request.UserPagingRequest;
import com.example.doanbe.payload.response.SuccessResponse;

public interface UserService {
    SuccessResponse getUsersPaging(UserPagingRequest userPagingRequest);
}
