package com.alvaro.vgym.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alvaro.vgym.R;
import com.alvaro.vgym.fragments.WorkoutDayFragment.OnWorkoutDaySelectedListener;
import com.alvaro.vgym.model.Workout;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Workout} and makes a call to the
 * specified {@link OnWorkoutDaySelectedListener}.
 */
public class WorkoutDayAdapter extends RecyclerView.Adapter<WorkoutDayAdapter.ViewHolder>
{

    private final List<Workout> workouts;
    private final OnWorkoutDaySelectedListener interactionListener;

    public WorkoutDayAdapter(List<Workout> workoutList, OnWorkoutDaySelectedListener listener)
    {
        workouts = workoutList;
        interactionListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.fragment_workout_day,
            parent,
            false
        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.workout = workouts.get(position);
        holder.workoutDay.setText(workouts.get(position).getDay());
        // Si es día de descanso ponemos "Descanso" como nombre
        if (workouts.get(position).isRestDay())
        {
            holder.workoutName.setText(holder.workoutView.getResources().getString(R.string.rest));
        }// Si el nombre está vacío ponemos un guión como nombre
        else if (workouts.get(position).getName().trim().isEmpty())
        {
            holder.workoutName.setText("-");
        }// Si no está vacío ponemos el nombre del entrenamiento
        else { holder.workoutName.setText(workouts.get(position).getName()); }

        if (workouts.get(position).isRestDay())
        {
            holder.workoutView.setBackgroundColor(
                holder.workoutView.getResources().getColor(R.color.restDay)
            );
        }

        holder.workoutView.setOnClickListener(v -> {
            if (null != interactionListener)
            {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                interactionListener.onWorkoutDaySelected(holder.workout);
            }
        });
    }

    @Override
    public int getItemCount() { return workouts.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View workoutView;
        public final TextView workoutDay;
        public final TextView workoutName;
        public Workout workout;

        public ViewHolder(View view)
        {
            super(view);
            workoutView = view;
            workoutDay = view.findViewById(R.id.workoutDay);
            workoutName = view.findViewById(R.id.workoutName);
        }

        @Override
        public String toString() { return super.toString() + " '" + workoutName.getText() + "'"; }
    }
}
