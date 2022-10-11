package com.sfn.securityservice.dao;

import com.sfn.securityservice.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser,Long> {
   public AppUser findByUsername(String username);
}
