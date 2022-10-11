package com.sfn.securityservice.service;

import com.sfn.securityservice.dao.AppRoleRepository;
import com.sfn.securityservice.dao.AppUserRepository;
import com.sfn.securityservice.entities.AppRole;
import com.sfn.securityservice.entities.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private AppUserRepository appUserRepository ;
    private AppRoleRepository appRoleRepository ;
    private PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AppUserRepository appUserRepository, AppRoleRepository appRoleRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser addNewUser(AppUser newUser) {
        String pw = newUser.getPassword();
        newUser.setPassword(passwordEncoder.encode(pw));
        return appUserRepository.save(newUser);
    }

    @Override
    public AppRole addNewRole(AppRole newRole) {
        return appRoleRepository.save(newRole);
    }

    @Override
    public void addRoleToUSer(String username, String roleName) {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findByRoleName(roleName);
        appUser.getAppRoles().add(appRole);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public List<AppUser> listUsers() {
        return appUserRepository.findAll();
    }
}
