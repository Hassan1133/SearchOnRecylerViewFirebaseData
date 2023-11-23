package com.example.fetchdata;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String phone;
    private String id;

    public User() {

    }

    public User(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
