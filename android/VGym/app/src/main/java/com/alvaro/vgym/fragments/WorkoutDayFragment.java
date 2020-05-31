package com.alvaro.vgym.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvaro.vgym.R;
import com.alvaro.vgym.adapters.WorkoutDayAdapter;
import com.alvaro.vgym.fragments.dummy.DummyContent.DummyItem;
import com.alvaro.vgym.model.Routine;
import com.alvaro.vgym.model.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnWorkoutDaySelectedListener}
 * interface.
 */
public class WorkoutDayFragment extends Fragment
{
    public static final String TAG = "workoutDayFragment";
    private static final String ARG_ROUTINE = "routine";

    private List<Workout> workoutList = null;

    private OnWorkoutDaySelectedListener listener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkoutDayFragment() { }

    @SuppressWarnings("unused")
    public static WorkoutDayFragment newInstance(List<Workout> workoutList)
    {
        WorkoutDayFragment fragment = new WorkoutDayFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ROUTINE, (ArrayList<? extends Parcelable>) workoutList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            workoutList = getArguments().getParcelableArrayList(ARG_ROUTINE);
        }
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {
        View view = inflater.inflate(
            R.layout.fragment_workout_day_list,
            container,
            false
        );

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new WorkoutDayAdapter(workoutList, listener));
        }

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof OnWorkoutDaySelectedListener)
        {
            listener = (OnWorkoutDaySelectedListener) context;

        } else
        {
            throw new RuntimeException(
                context.toString() + " must implement OnWorkoutDaySelectedListener"
            );
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnWorkoutDaySelectedListener { void onWorkoutDaySelected(Workout workout); }
}
