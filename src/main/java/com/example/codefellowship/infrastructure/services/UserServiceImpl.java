package com.example.codefellowship.infrastructure.services;

import com.example.codefellowship.domain.ApplicationUser;
import com.example.codefellowship.infrastructure.ApplicationUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService{
    @Autowired
    ApplicationUserRepo applicationUserRepo;


    @Override
    public ApplicationUser findApplicationUserByUsername(String username) {
        return applicationUserRepo.findApplicationUserByUsername(username);
    }

    @Override
    public List<ApplicationUser> findAll() {
        return applicationUserRepo.findAll();
    }

    @Override
    public ApplicationUser findById(Long id) {
        return applicationUserRepo.findById(id).orElseThrow();
    }

    @Override
    public void save(ApplicationUser applicationUser) {
        applicationUserRepo.save(applicationUser);
    }
}
