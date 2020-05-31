package com.alvaro.vgym.model;

import android.content.Context;

import com.alvaro.vgym.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Routine implements Serializable
{
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("workouts")
    private List<Workout> workouts;

    public Routine()
    {
        this.name = "";

        String[] days = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        };

        this.workouts = new ArrayList<Workout>();

        for (int i = 0; i < 7; i++) { this.workouts.add(new Workout(i, days[i])); }
    }

    public Routine(Context ctx)
    {
        this.name = "Prueba";
        // Obtenemos los días de la semana
        String[] days = ctx.getResources().getStringArray(R.array.days_array);

        this.workouts = new ArrayList<Workout>();
        // Poblamos la lista
        for (int i = 0; i < 7; i++) { this.workouts.add(new Workout(i, days[i])); }
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<Workout> getWorkouts() { return workouts; }

    public void setWorkouts(List<Workout> workouts) { this.workouts = workouts; }
}
