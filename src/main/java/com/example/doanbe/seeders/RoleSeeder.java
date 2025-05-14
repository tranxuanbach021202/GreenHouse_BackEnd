//package com.example.doanbe.seeders;
//import com.example.doanbe.document.Role;
//import com.example.doanbe.enums.ERole;
//import com.example.doanbe.repository.RoleRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//public class RoleSeeder implements CommandLineRunner {
//
//    private final RoleRepository roleRepository;
//
//    public RoleSeeder(RoleRepository roleRepository) {
//        this.roleRepository = roleRepository;
//    }
//
//    @Override
//    public void run(String... args) {
//        List<ERole> roles = Arrays.asList(ERole.ROLE_USER, ERole.ROLE_ADMIN);
//
//        for (ERole role : roles) {
//            if (roleRepository.findByName(role).isEmpty()) {
//                roleRepository.save(new Role(role));
//                System.out.println("Đã thêm role: " + role);
//            }
//            System.out.println("ko thêm role: " + role);
//        }
//    }
//}
//
