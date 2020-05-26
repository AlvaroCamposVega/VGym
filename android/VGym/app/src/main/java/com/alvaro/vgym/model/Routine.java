package com.alvaro.vgym.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Routine implements Serializable
{
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("weekdays")
    private ArrayList<Workout> weekdays;

    public Routine()
    {
        this.name = "";

        String[] days = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        };

        this.weekdays = new ArrayList<Workout>();

        for (int i = 0; i < 7; i++) { this.weekdays.add(new Workout(i, days[i])); }
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public ArrayList<Workout> getWeekdays() { return weekdays; }

    public void setWeekdays(ArrayList<Workout> weekdays) { this.weekdays = weekdays; }
}
