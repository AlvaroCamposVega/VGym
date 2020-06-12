package com.alvaro.vgym.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alvaro.vgym.R;
import com.alvaro.vgym.model.Exercise;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Exercise}
 */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder>
{
    private final List<Exercise> exercises;

    public WorkoutAdapter(List<Exercise> exerciseList) { exercises = exerciseList; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_workout_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.exercise = exercises.get(position);
        holder.exerciseName.setText(exercises.get(position).getName());
        holder.exerciseInfo.setText(
            exercises.get(position).getSets() + " x " + exercises.get(position).getReps()
        );
    }

    @Override
    public int getItemCount() { return exercises.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView exerciseName;
        public final TextView exerciseInfo;
        public Exercise exercise;

        public ViewHolder(View view)
        {
            super(view);
            exerciseName = view.findViewById(R.id.adapterExerciseName);
            exerciseInfo = view.findViewById(R.id.adapterExerciseInfo);
        }

        @Override
        public String toString() { return super.toString() + " '" + exerciseInfo.getText() + "'"; }
    }
}
