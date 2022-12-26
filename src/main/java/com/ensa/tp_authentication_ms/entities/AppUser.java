package com.ensa.tp_authentication_ms.entities;


import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String username;

    //qd qlq1 envoie password je le stoque ds cette variable,mais qd je le genere je l'ignore =caché au moment de creation==post
    @JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Boolean actived;


    //EAGER: chaque fois que je consulte un utilisateur, j'ai besoin de consulter ses roles
    @ManyToMany(fetch=FetchType.EAGER)
    private Collection<AppRole> roles = new ArrayList<>();
}