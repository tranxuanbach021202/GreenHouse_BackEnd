package com.example.doanbe.services.Impl;

import com.example.doanbe.dto.Pagination;
import com.example.doanbe.dto.UserDto;
import com.example.doanbe.document.User;
import com.example.doanbe.dto.request.UpdateProfileRequest;
import com.example.doanbe.dto.response.UserProfileResponse;
import com.example.doanbe.exception.AppException;
import com.example.doanbe.payload.request.UserPagingRequest;
import com.example.doanbe.payload.response.SuccessResponse;
import com.example.doanbe.repository.UserRepository;
import com.example.doanbe.services.UserDetailsImpl;
import com.example.doanbe.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public SuccessResponse getUsersPaging(UserPagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(
                        request.getSortDir().equalsIgnoreCase("desc")
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC,
                        request.getSortBy()
                )
        );

        Query query = new Query().with(pageable);
        query.addCriteria(Criteria.where("isVerified").is(true));

        if (request.getSearch() != null && !request.getSearch().isEmpty()) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("username").regex(request.getSearch(), "i"),
                    Criteria.where("email").regex(request.getSearch(), "i")
            ));
        }

        List<User> users = mongoTemplate.find(query, User.class);
        long totalUsers = mongoTemplate.count(query.skip(-1).limit(-1), User.class);

        List<UserDto> userList = users.stream()
                .map(user -> mapper.map(user, UserDto.class))
                .collect(Collectors.toList());

        if (userList.isEmpty()) {
            throw new AppException(404, 44,
                    "Không tìm thấy user nào thỏa mãn điều kiện tìm kiếm!");
        }

        Page<User> userPage = new PageImpl<>(users, pageable, totalUsers);

        Pagination meta = new Pagination();
        meta.setPage(userPage.getNumber());
        meta.setSize(userPage.getSize());
        meta.setTotalElements(userPage.getTotalElements());
        meta.setTotalPages(userPage.getTotalPages());
        meta.setHasNext(userPage.hasNext());
        meta.setHasPrevious(userPage.hasPrevious());

        return new SuccessResponse(userList, meta);
    }

    @Override
    public SuccessResponse getCurrentUserProfile() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfileResponse userProfileResponse = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getUrlAvatar(),
                user.getBio()
        );
        return new SuccessResponse(userProfileResponse);
    }

    @Override
    public void updateProfile(UpdateProfileRequest request) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setDisplayName(request.getDisplayName());
        user.setUrlAvatar(request.getUrlAvatar());
        user.setBio(request.getBio());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}


