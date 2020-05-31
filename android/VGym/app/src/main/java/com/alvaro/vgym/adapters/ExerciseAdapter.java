package com.alvaro.vgym.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvaro.vgym.R;
import com.alvaro.vgym.fragments.ExercisesFragment.OnExerciseSelectedListener;
import com.alvaro.vgym.fragments.dummy.DummyContent.DummyItem;
import com.alvaro.vgym.model.Exercise;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Exercise} and makes a call to the
 * specified {@link OnExerciseSelectedListener}.
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder>
{

    private final List<Exercise> exercises;
    private final OnExerciseSelectedListener interactionListener;

    public ExerciseAdapter(List<Exercise> exerciseList, OnExerciseSelectedListener listener)
    {
        exercises = exerciseList;
        interactionListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.fragment_exercises,
            parent,
            false
        );

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

        holder.exerciseView.setOnClickListener(v -> {
            if (null != interactionListener)
            {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                interactionListener.onExerciseSelected(holder.exercise);
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (null != interactionListener)
            {
                interactionListener.onDeleteExercise(holder.exercise);
            }
        });
    }

    @Override
    public int getItemCount() { return exercises.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View exerciseView;
        public final ImageView deleteBtn;
        public final TextView exerciseName;
        public final TextView exerciseInfo;
        public Exercise exercise;

        public ViewHolder(View view)
        {
            super(view);
            exerciseView = view;
            deleteBtn = view.findViewById(R.id.exerciseDeleteBtn);
            exerciseName = view.findViewById(R.id.exerciseName);
            exerciseInfo = view.findViewById(R.id.exerciseInfo);
        }

        @Override
        public String toString() { return super.toString() + " '" + exerciseInfo.getText() + "'"; }
    }
}
