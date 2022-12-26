package com.ensa.tp_authentication_ms.service;

import com.ensa.tp_authentication_ms.entities.AppRole;
import com.ensa.tp_authentication_ms.entities.AppUser;

public interface AccountService {

    public AppUser saveUser(String username, String password, String confirmedPassword );
    public AppRole save(AppRole role);
    public AppUser loadUserByUsername(String username);
    public void addRoleToUser(String username, String rolename);
}