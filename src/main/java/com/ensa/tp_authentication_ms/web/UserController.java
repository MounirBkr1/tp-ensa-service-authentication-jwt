package com.ensa.tp_authentication_ms.web;

import com.ensa.tp_authentication_ms.entities.AppUser;
import com.ensa.tp_authentication_ms.service.AccountService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public AppUser register(@RequestBody UserForm userForm){
        return accountService.saveUser(userForm.getUsername(), userForm.getPassword(),userForm.getConfirmedPassword());
    }
}


@Data
class UserForm{
    private String username;
    private String password;
    private String confirmedPassword;
}