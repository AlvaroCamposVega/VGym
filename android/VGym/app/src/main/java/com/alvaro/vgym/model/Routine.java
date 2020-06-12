package com.alvaro.vgym.model;

import android.content.Context;

import com.alvaro.vgym.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Routine implements Serializable, Comparable
{
    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("favorite")
    private boolean favorite;

    @Expose
    @SerializedName("selected")
    private boolean selected;

    @Expose
    @SerializedName("workouts")
    private List<Workout> workouts;

    public Routine() { }

    public Routine(Context ctx)
    {
        this.id = "";
        this.name = "";
        this.favorite = false;
        this.selected = false;
        // Obtenemos los días de la semana
        String[] days = ctx.getResources().getStringArray(R.array.days_array);

        this.workouts = new ArrayList<Workout>();
        // Poblamos la lista
        for (int i = 0; i < 7; i++) { this.workouts.add(new Workout(i, days[i])); }
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public boolean isFavorite() { return favorite; }

    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public boolean isSelected() { return selected; }

    public void setSelected(boolean selected) { this.selected = selected; }

    public List<Workout> getWorkouts() { return workouts; }

    public void setWorkouts(List<Workout> workouts) { this.workouts = workouts; }

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof Routine)
        {
            Routine obj = (Routine) o;

            return this.name.toLowerCase().compareTo(obj.getName().toLowerCase());
        }

        return 0;
    }

    /*@NonNull
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }*/

    @Override
    public boolean equals(Object o)
    {
        boolean equals = false;

        if (o instanceof Routine)
        {
            Routine obj = (Routine) o;
            equals = this.id.equals(obj.getId()) && this.name.equals(obj.getName())
                    && this.workouts.equals(obj.getWorkouts());
        }

        return equals;
    }
}
