package com.alvaro.vgym.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Workout implements Serializable, Parcelable
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
    @SerializedName("rest")
    private boolean rest;

    @Expose
    @SerializedName("exercises")
    private List<Exercise> exercises;

    public Workout() { }

    public Workout(int id, String day)
    {
        this.id = id;
        this.day = day;
        this.name = "";
        this.rest = false;
        this.exercises = new ArrayList<Exercise>();
        /*this.exercises.add(new Exercise(0, "Flexiones", 4, 12));
        this.exercises.add(new Exercise(1, "Flexiones diamante", 4, 12));
        this.exercises.add(new Exercise(2, "Extensión tríceps", 4, 12));*/
    }

    protected Workout(Parcel in)
    {
        id = in.readInt();
        name = in.readString();
        day = in.readString();
        rest = in.readByte() != 0;
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>()
    {
        @Override
        public Workout createFromParcel(Parcel in)
        {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size)
        {
            return new Workout[size];
        }
    };

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDay() { return day; }

    public void setDay(String day) { this.day = day; }

    public boolean isRestDay() { return rest; }

    public void setRest(boolean rest) { this.rest = rest; }

    public List<Exercise> getExercises() { return exercises; }

    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) { dest.writeSerializable(this); }
}
