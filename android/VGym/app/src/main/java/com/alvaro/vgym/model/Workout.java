package com.alvaro.vgym.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Workout implements Serializable
{
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("day")
    private String day;

    @Expose
    @SerializedName("restDay")
    private boolean restDay;

    @Expose
    @SerializedName("exercises")
    private List<Exercise> exercises;

    public Workout() { }

    public Workout(int id, String day)
    {
        this.id = id;
        this.day = day;
        this.name = "";
        this.restDay = false;
        this.exercises = new ArrayList<>();
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDay() { return day; }

    public void setDay(String day) { this.day = day; }

    public boolean isRestDay() { return restDay; }

    public void setRestDay(boolean restDay) { this.restDay = restDay; }

    public List<Exercise> getExercises() { return exercises; }

    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    public void copyExercises(List<Exercise> exercises)
    {
        this.exercises = new ArrayList<>();

        for (Exercise exercise : exercises)
        {
            this.exercises.add(new Exercise(
                exercise.getId(), exercise.getName(), exercise.getSets(), exercise.getReps()
            ));
        }
    }
}
