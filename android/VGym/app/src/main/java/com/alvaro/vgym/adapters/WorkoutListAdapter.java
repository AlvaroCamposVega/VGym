package com.alvaro.vgym.adapters;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alvaro.vgym.R;
import com.alvaro.vgym.fragments.WorkoutListFragment;
import com.alvaro.vgym.fragments.WorkoutListFragment.OnWorkoutSelectedListener;
import com.alvaro.vgym.model.Workout;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Workout} and makes a call to the
 * specified {@link OnWorkoutSelectedListener}.
 */
public class WorkoutListAdapter extends RecyclerView.Adapter<WorkoutListAdapter.ViewHolder>
{

    private final List<Workout> workouts;
    private final WorkoutListFragment.OnWorkoutSelectedListener interactionListener;

    public WorkoutListAdapter(List<Workout> workoutList, OnWorkoutSelectedListener listener)
    {
        workouts = workoutList;
        interactionListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.fragment_workout_list_adapter,
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

            /*holder.workoutView.setBackgroundColor(
                holder.workoutView.getResources().getColor(R.color.restDay)
            );*/
        } // Si el nombre está vacío ponemos un guión como nombre
        else if (workouts.get(position).getName().trim().isEmpty())
        {
            holder.workoutName.setText("-");
        } // Si no está vacío ponemos el nombre del entrenamiento
        else { holder.workoutName.setText(workouts.get(position).getName()); }

        holder.workoutView.setOnClickListener(v -> {
            if (null != interactionListener)
            {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                interactionListener.onWorkoutSelected(holder.workout);
            }
        });

        if (workouts.get(position).isRestDay())
        {
            holder.workoutView.setBackgroundColor(
                holder.workoutView.getResources().getColor(R.color.restDay)
            );
        }
        else
        {
            switch (holder.workoutView.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK
            )
            {
                case Configuration.UI_MODE_NIGHT_YES:
                    holder.workoutView.setBackgroundColor(
                        holder.workoutView.getResources().getColor(R.color.backgroundD)
                    );
                    break;
                default:
                    holder.workoutView.setBackgroundColor(
                        holder.workoutView.getResources().getColor(R.color.backgroundL)
                    );
            }
        }
    }

    @Override
    public int getItemCount() { return workouts.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener
    {
        public final View workoutView;
        public final TextView workoutDay;
        public final TextView workoutName;
        public Workout workout;

        public ViewHolder(View view)
        {
            super(view);
            workoutView = view;
            workoutDay = view.findViewById(R.id.adapterWorkoutDay);
            workoutName = view.findViewById(R.id.adapterWorkoutName);

            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public String toString() { return super.toString() + " '" + workoutName.getText() + "'"; }

        @Override
        public void onCreateContextMenu(
            ContextMenu menu,
            View v,
            ContextMenu.ContextMenuInfo menuInfo
        )
        {   // API 23 AND ABOVE
            menu.setHeaderTitle(R.string.options_label);

            MenuItem menuItem1 = menu.add(0, workout.getId(), 0, R.string.copy_label)
                .setOnMenuItemClickListener(item -> {
                    interactionListener.onCopyWorkout(item.getItemId());

                    return true;
                });

            MenuItem menuItem2 = menu.add(0, workout.getId(), 0, R.string.clear_label)
                .setOnMenuItemClickListener(item -> {
                    interactionListener.onClearWorkout(item.getItemId());

                    return true;
            });

            if (workout.getName().trim().isEmpty() && workout.getExercises().isEmpty()
                && !workout.isRestDay())
            {
                menuItem1.setEnabled(false);
                menuItem2.setEnabled(false);
            }
        }
    }
}
