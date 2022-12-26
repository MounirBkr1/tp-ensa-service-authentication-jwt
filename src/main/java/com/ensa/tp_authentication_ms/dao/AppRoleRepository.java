package com.ensa.tp_authentication_ms.dao;

import com.ensa.tp_authentication_ms.entities.AppRole;
import com.ensa.tp_authentication_ms.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface AppRoleRepository extends JpaRepository<AppRole,Long> {
    public AppRole findByRoleName(String username);
}