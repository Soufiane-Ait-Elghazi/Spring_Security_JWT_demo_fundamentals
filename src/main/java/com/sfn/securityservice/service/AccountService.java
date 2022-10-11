package com.sfn.securityservice.service;

import com.sfn.securityservice.entities.AppRole;
import com.sfn.securityservice.entities.AppUser;

import java.util.List;

public interface AccountService {
    public AppUser addNewUser(AppUser newUser);
    public AppRole addNewRole(AppRole newRole);
    public void addRoleToUSer(String username,String roleName);
    public AppUser loadUserByUsername(String username);
    public List<AppUser> listUsers();
}
