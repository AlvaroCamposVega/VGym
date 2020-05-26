package com.alvaro.vgym.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Workout implements Serializable
{
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("rest")
    private boolean rest;

    @Expose
    @SerializedName("exercises")
    private ArrayList<Exercise> exercises;

    public Workout() { }

    public Workout(int id, String name)
    {
        this.id = id;
        this.name = name;
        this.rest = false;
        this.exercises = new ArrayList<Exercise>();
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public boolean isRest() { return rest; }

    public void setRest(boolean rest) { this.rest = rest; }

    public ArrayList<Exercise> getExercises() { return exercises; }

    public void setExercises(ArrayList<Exercise> exercises) { this.exercises = exercises; }
}
