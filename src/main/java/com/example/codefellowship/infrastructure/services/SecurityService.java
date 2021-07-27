package com.example.codefellowship.infrastructure.services;


import com.example.codefellowship.domain.Role;

public interface SecurityService {

    Role findRoleById(Long roleId);
    Role findRoleByName(String name);
}
