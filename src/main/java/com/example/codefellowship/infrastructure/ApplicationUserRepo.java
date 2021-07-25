package com.example.codefellowship.infrastructure;

import com.example.codefellowship.domain.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationUserRepo extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findApplicationUserByUsername(String username);
}
