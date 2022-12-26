package com.ensa.tp_authentication_ms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//WebSecurityConfigurerAdapter compatible with < 2.6.14 version of spring boot
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private  UserDetailsService userDetailsService;
    @Autowired
    private  BCryptPasswordEncoder bCryptPasswordEncoder;



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //use UserDetailService of srping to compare the 2 password
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        //super.configure(auth);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin();


        http.csrf().disable();
        //statless: on va pas utiliser les sessions
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/login/**","/register/**").permitAll();
        http.authorizeRequests().antMatchers("/appUsers/**", "/appRoles/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().anyRequest().authenticated();

        //add filters
        http.addFilter(new JWTAuthenticationFilter(authenticationManager()));
        //pour chaque requete on verifie la signature du token
        http.addFilterBefore(new JWTAuthorizationFilter(),UsernamePasswordAuthenticationFilter.class);

    }



}