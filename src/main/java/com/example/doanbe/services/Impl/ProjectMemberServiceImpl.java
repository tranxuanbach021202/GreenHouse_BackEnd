package com.example.doanbe.services.Impl;

import com.example.doanbe.document.ProjectMember;
import com.example.doanbe.enums.InvitationStatus;
import com.example.doanbe.exception.AppException;
import com.example.doanbe.repository.ProjectMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectMemberServiceImpl {

    @Autowired
    private ProjectMemberRepository memberRepo;


}
