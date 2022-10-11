package com.sfn.securityservice.dao;

import com.sfn.securityservice.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole,Long> {
  public AppRole findByRoleName(String roleName);

}
