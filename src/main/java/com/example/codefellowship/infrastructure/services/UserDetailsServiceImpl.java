package com.example.codefellowship.infrastructure.services;

import com.example.codefellowship.domain.ApplicationUser;
import com.example.codefellowship.infrastructure.ApplicationUserRepo;
import com.example.codefellowship.infrastructure.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    ApplicationUserRepo applicationUserRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser student = applicationUserRepo.findApplicationUserByUsername(username);

        if (student == null) {
            System.out.print("Username not found");
            throw new UsernameNotFoundException((username + " not found"));
        }

//        return student;
        return new org.springframework.security.core.userdetails.User(
                student.getUsername(), student.getPassword(), student.isEnabled(), true, true,
                true, student.getAuthorities());
    }

//    @Override
//    public UserDetails loadUserByUsername(String email)
//            throws UsernameNotFoundException {
//
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            return new org.springframework.security.core.userdetails.User(
//                    " ", " ", true, true, true, true,
//                    getAuthorities(Arrays.asList(
//                            roleRepository.findByName("ROLE_USER"))));
//        }
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getEmail(), user.getPassword(), user.isEnabled(), true, true,
//                true, getAuthorities(user.getRoles()));
//    }
}