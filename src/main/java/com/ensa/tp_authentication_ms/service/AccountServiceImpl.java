package com.ensa.tp_authentication_ms.service;

import com.ensa.tp_authentication_ms.dao.AppRoleRepository;
import com.ensa.tp_authentication_ms.dao.AppUserRepository;
import com.ensa.tp_authentication_ms.entities.AppRole;
import com.ensa.tp_authentication_ms.entities.AppUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    //injection des dependances via constructor
    private final AppUserRepository appUserRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AppRoleRepository appRoleRepository;
    public AccountServiceImpl(AppUserRepository appUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AppRoleRepository appRoleRepository) {
        this.appUserRepository = appUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.appRoleRepository = appRoleRepository;
    }


    //add user
    @Override
    public AppUser saveUser(String username, String password, String confirmedPassword) {
        AppUser user=appUserRepository.findByUsername(username);
        if(user != null) throw new RuntimeException("User already exists");
        if(!password.equals(confirmedPassword)) throw new RuntimeException("Please confirm password");

        //user existe pas , creer un user
        AppUser appUser= new AppUser();
        appUser.setUsername(username);
        appUser.setActived(true);
        appUser.setPassword(bCryptPasswordEncoder.encode(password));
        appUserRepository.save(appUser);
        //add role to user by default
        addRoleToUser(username, "USER");

        return appUser;
    }

    @Override
    public AppRole save(AppRole role) {
        return appRoleRepository.save(role);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public void addRoleToUser(String username, String rolename) {
        AppUser appUser= appUserRepository.findByUsername(username);
        AppRole appRole= appRoleRepository.findByRoleName(rolename);
        //add role to user
        appUser.getRoles().add(appRole);
    }



}