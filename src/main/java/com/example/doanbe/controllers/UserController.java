package com.example.doanbe.controllers;

import com.example.doanbe.dto.request.UpdateProfileRequest;
import com.example.doanbe.dto.response.UserProfileResponse;
import com.example.doanbe.payload.request.UserPagingRequest;
import com.example.doanbe.payload.response.MessageResponse;
import com.example.doanbe.payload.response.SuccessResponse;
import com.example.doanbe.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<SuccessResponse> getUserPaging(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search
    ) {
        UserPagingRequest request = UserPagingRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .search(search).build();
        return ResponseEntity.ok(userService.getUsersPaging(request));
    }

    @PutMapping("/profile")
    public ResponseEntity<MessageResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        userService.updateProfile(request);
        return ResponseEntity.ok(new MessageResponse("Cập Nhật Profile Thành công"));
    }


    @GetMapping("/profile")
    public ResponseEntity<SuccessResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

}
