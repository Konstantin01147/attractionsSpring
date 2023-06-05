package com.example.attractionsSpring.model;

import javax.persistence.*;

@Entity(name = "adminDataTable")
public class Admin {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    @Column
    private String login;
    @Column
    private String password;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Admin(String login, String password) {

        this.login = login;
        this.password = password;
    }
}
