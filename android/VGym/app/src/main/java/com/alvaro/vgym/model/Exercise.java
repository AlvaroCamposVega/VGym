package com.alvaro.vgym.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Exercise implements Serializable
{
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("sets")
    private int sets;

    @Expose
    @SerializedName("reps")
    private int reps;

    public Exercise() { }

    public Exercise(int id, String name, int sets, int reps)
    {
        this.id = id;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getSets() { return sets; }

    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }

    public void setReps(int reps) { this.reps = reps; }

    @Override
    public boolean equals(Object o)
    {
        boolean equals = false;

        if (o instanceof Exercise)
        {
            Exercise obj = (Exercise) o;
            equals = this.id == obj.getId();
        }

        return equals;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() { return Objects.hash(id); }
}
