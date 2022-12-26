package com.ensa.tp_authentication_ms.security;

import com.ensa.tp_authentication_ms.entities.AppUser;
import com.ensa.tp_authentication_ms.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

//UserDetailsServiceImpl class qui implemente UserDetailsService du spring security
//@Service: instanciée au demarrage en tant que service

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AccountService accountService;

    //if you want to see if an user exist or not
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser= accountService.loadUserByUsername(username);

        if(appUser == null) throw new UsernameNotFoundException("user dont exist");
        //retrieve roles from user finded
        Collection<GrantedAuthority> authorities= new ArrayList<>();
        appUser.getRoles().forEach(r->{
            authorities.add(new SimpleGrantedAuthority((r.getRoleName())));
        });

        //transformer data to user fourni par spring
        return new User(appUser.getUsername(),appUser.getPassword(),authorities);
    }
}