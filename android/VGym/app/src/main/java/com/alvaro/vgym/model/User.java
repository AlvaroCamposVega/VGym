package com.alvaro.vgym.model;

import java.io.Serializable;

public class User implements Serializable
{
    private String name;
    private String surname;

    public User() { }

    public User(String name, String surname) { this.name = name; this.surname = surname; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getSurName() { return surname; }

    public void setSurName(String surName) { this.surname = surName; }

    @Override
    public String toString() { return name + " " + surname; }
}
