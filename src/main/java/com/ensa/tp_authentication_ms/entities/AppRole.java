package com.ensa.tp_authentication_ms.entities;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.GenericArrayType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppRole {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String roleName;

}