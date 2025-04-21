package com.example.doanbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DoAnBeApplication {

    public static void main(String[] args) {
        System.out.println("Đã thêm role 1: ");
        SpringApplication.run(DoAnBeApplication.class, args);
    }

}
