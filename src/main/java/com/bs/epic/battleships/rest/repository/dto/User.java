package com.bs.epic.battleships.rest.repository.dto;

import com.bs.epic.battleships.documentation.annotations.Doc;
import com.bs.epic.battleships.documentation.annotations.DocIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@DynamicUpdate
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocIgnore
    public long id;

    @Doc("The user's chosen username")
    public String username;
    @Doc("The user's chosen email")
    public String email;

    @Doc("The amount of single player games that the user has won")
    public int spWins = 0;
    @Doc("The amount of multiplayer games that the user has won")
    public int mpWins = 0;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @DocIgnore
    public String password;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User() { }
}
