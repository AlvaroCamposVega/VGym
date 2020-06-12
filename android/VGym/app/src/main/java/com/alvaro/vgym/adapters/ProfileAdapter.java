package com.alvaro.vgym.adapters;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvaro.vgym.R;
import com.alvaro.vgym.fragments.ProfileFragment.OnRoutineInteractionListener;
import com.alvaro.vgym.model.Routine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Routine} and makes a call to the
 * specified {@link OnRoutineInteractionListener}.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>
{
    private static final int LAYOUT_SELECTED = 222;
    private static final int LAYOUT_FAVORITES = 333;
    private static final int LAYOUT_ALL = 444;

    private int layout;
    private int cursor;

    private final List<Routine> routines;
    private Routine selectedRoutine;
    private final List<Routine> favoriteRoutines;
    private final OnRoutineInteractionListener interactionListener;

    public ProfileAdapter(List<Routine> routineList, OnRoutineInteractionListener listener)
    {
        routines = routineList;
        favoriteRoutines = new ArrayList<Routine>();
        layout = LAYOUT_SELECTED;
        cursor = 0;
        interactionListener = listener;

        if (!routines.isEmpty())
        {
            Collections.sort(routines);

            for (Routine routine: routines)
            {
                if (routine.isSelected()) { selectedRoutine = routine; }

                if (routine.isFavorite()) { favoriteRoutines.add(routine); }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;

        if (cursor < 1)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_profile_adapter,
                parent,
                false
            );
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_profile_multi_adapter,
                parent,
                false
            );
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        if (layout == LAYOUT_SELECTED)
        {
            if (selectedRoutine != null)
            {
                holder.routine = selectedRoutine;
                holder.routineName.setText(holder.routine.getName());

                if (holder.routine.isFavorite())
                {
                    holder.favoriteBtn.setImageResource(R.drawable.ic_star_black_24dp);
                }

                setEditFavoriteAndDeleteListeners(holder);
            }

            layout = LAYOUT_FAVORITES;
        }
        else if (layout == LAYOUT_FAVORITES)
        {
            if (cursor == 0) { holder.title.setText(R.string.favourites_routines_label); }

            if (favoriteRoutines.isEmpty())
            {
                holder.routineContainer.setVisibility(View.GONE);
                holder.hint.setVisibility(View.VISIBLE);

                layout = LAYOUT_ALL;
            }
            else
            {
                holder.routine = favoriteRoutines.get(position - 1);
                holder.routineName.setText(holder.routine.getName());
                holder.favoriteBtn.setImageResource(R.drawable.ic_star_black_24dp);

                if (!holder.routine.isSelected())
                {
                    holder.selectBtn.setBackgroundResource(R.drawable.select_btn_bottom_shadow);

                    holder.selectBtn.setOnClickListener(v -> {
                        if (null != interactionListener)
                        {
                            interactionListener.onChangeSelectedRoutine(holder.routine);
                        }
                    });
                }

                setEditFavoriteAndDeleteListeners(holder);

                cursor ++;

                if (cursor >= favoriteRoutines.size()) { layout = LAYOUT_ALL; cursor = 0; }
            }
        }
        else
        {
            int updatedPosition = (position - 1 - favoriteRoutines.size());

            if (favoriteRoutines.isEmpty()) { updatedPosition --; }

            holder.routine = routines.get(updatedPosition);

            if (cursor == 0) { holder.title.setText(R.string.all_routines_label); }

            holder.routineName.setText(holder.routine.getName());

            if (holder.routine.isFavorite())
            {
                holder.favoriteBtn.setImageResource(R.drawable.ic_star_black_24dp);
            }

            if (!holder.routine.isSelected())
            {
                holder.selectBtn.setBackgroundResource(R.drawable.select_btn_bottom_shadow);

                holder.selectBtn.setOnClickListener(v -> {
                    if (null != interactionListener)
                    {
                        interactionListener.onChangeSelectedRoutine(holder.routine);
                    }
                });
            }

            setEditFavoriteAndDeleteListeners(holder);

            cursor ++;
        }
    }

    private void setEditFavoriteAndDeleteListeners(ProfileAdapter.ViewHolder holder)
    {
        holder.editBtn.setOnClickListener(v -> {
            if (null != interactionListener) { interactionListener.onEditRoutine(holder.routine); }
        });

        holder.favoriteBtn.setOnClickListener(v -> {
            if (null != interactionListener)
            {
                interactionListener.onAddRoutineToFavorites(holder.routine);
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (null != interactionListener) {
                interactionListener.onDeleteRoutine(holder.routine.getId(), routines);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        int size;

        if (routines.isEmpty()) { size = routines.size(); }
        else
        {
            size = routines.size() + favoriteRoutines.size() + 1;

            if (favoriteRoutines.isEmpty()) { size ++; }
        }

        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView title;
        public final TextView hint;
        public final ConstraintLayout routineContainer;
        public final View editBtn;
        public final TextView routineName;
        public final ImageView favoriteBtn;
        public final View selectBtn;
        public final View deleteBtn;
        public Routine routine;

        public ViewHolder(View view)
        {
            super(view);
            title = view.findViewById(R.id.adapterTitle);
            hint = view.findViewById(R.id.adapterHint);
            routineContainer = view.findViewById(R.id.adapterRoutine);
            editBtn = view.findViewById(R.id.adapterEditBtn);
            routineName = view.findViewById(R.id.adapterRoutineName);
            favoriteBtn = view.findViewById(R.id.adapterFavoriteBtn);
            selectBtn = view.findViewById(R.id.adapterSelectBtn);
            deleteBtn = view.findViewById(R.id.adapterDeleteBtn);
        }

        @Override
        public String toString() { return super.toString() + " '" + title.getText() + "'"; }
    }
}
