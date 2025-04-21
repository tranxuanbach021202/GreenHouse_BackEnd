package com.example.doanbe.factory;

import com.example.doanbe.enums.ERole;
import com.example.doanbe.document.Role;
import com.example.doanbe.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.relation.RoleNotFoundException;

@Component
public class RoleFactory {
    @Autowired
    RoleRepository roleRepository;

    public Role getInstance(String role) throws RoleNotFoundException {
        switch (role) {
            case "admin" -> {
                return roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RoleNotFoundException("Không tìm thấy ROLE_ADMIN"));
            }
            case "user" -> {
                return roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RoleNotFoundException("Không tìm thấy ROLE_USER"));
            }
            default -> throw new RoleNotFoundException("Không tìm thấy role: " + role);
        }
    }
}
