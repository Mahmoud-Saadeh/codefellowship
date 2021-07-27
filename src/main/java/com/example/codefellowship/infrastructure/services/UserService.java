package com.example.codefellowship.infrastructure.services;

import com.example.codefellowship.domain.ApplicationUser;

import java.util.List;

public interface UserService {
    ApplicationUser findApplicationUserByUsername(String username);

    List<ApplicationUser> findAll();

    ApplicationUser findById(Long id);

    void save(ApplicationUser applicationUser);
}
