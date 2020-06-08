package com.alvaro.vgym.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alvaro.vgym.R;
import com.alvaro.vgym.adapters.WorkoutAdapter;
import com.alvaro.vgym.model.Routine;
import com.alvaro.vgym.model.Workout;
import com.alvaro.vgym.services.FirebaseService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A fragment representing a list of Items.
 */
public class WorkoutFragment extends Fragment
{
    public static final String TAG = "workoutFragment";

    private Routine routine;
    private Workout todayWorkout;
    private String selectedRoutineKey;
    private MaterialToolbar topAppBar;
    private WorkoutAdapter adapter;
    private FirebaseService firebaseService;

    private TextView loading;
    private TextView hint;
    private ConstraintLayout workoutProperties;

    private TextView workoutDay;
    private TextView workoutName;
    private TextView routineName;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkoutFragment() { }

    @SuppressWarnings("unused")
    public static WorkoutFragment newInstance() { return new WorkoutFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        firebaseService = FirebaseService.getInstance();
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {
        View view = inflater.inflate(R.layout.fragment_workout_layout, container, false);

        topAppBar = view.findViewById(R.id.fragmentTopAppBar);
        setTopAppBarListener();

        loading = view.findViewById(R.id.workoutLoading);
        hint = view.findViewById(R.id.workoutHint);
        workoutProperties = view.findViewById(R.id.workoutProperties);

        workoutDay = view.findViewById(R.id.workoutDay);
        workoutName = view.findViewById(R.id.workoutName);
        routineName = view.findViewById(R.id.workoutRoutineName);

        getData(view);

        return view;
    }

    /**
     * Establece el listener de la top app bar para este fragmento.
     */
    private void setTopAppBarListener()
    {   // Listener
        topAppBar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId())
            {
                case R.id.mnuMainEditRoutine:
                    RoutineDialogFragment routineDialog = RoutineDialogFragment.newInstance(
                        routine
                    );

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                    routineDialog.show(fragmentManager, RoutineDialogFragment.TAG);
                    break;
                case R.id.mnuMainAddToFavorites:
                    Log.i("VGym", "Click en Añadir a Favoritos!");
                    break;
                case R.id.mnuMainLogOut:
                    // Cerramos la sesión
                    firebaseService.signOut();
                    getActivity().finish(); // Finalizamos la actividad
                    break;
            }

            return false;
        });
    }

    private void getData(View view)
    {
        String uid = firebaseService.getUid();

        DatabaseReference dbSelectedRef = firebaseService.getReference("selected/" + uid);
        // Si el usuario no tiene una rutina seleccionada significa que no tiene ninguna rutina ya
        // que es obligatorio tener una seleccionada
        dbSelectedRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                selectedRoutineKey = dataSnapshot.getValue(String.class);

                if (selectedRoutineKey != null)
                {
                    String reference = "routines/" + uid + "/" + selectedRoutineKey;

                    DatabaseReference dbRef = firebaseService.getReference(reference);
                    // Obtenemos la rutina seleccionada
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            routine = dataSnapshot.getValue(Routine.class);
                            // De la rutina mostramos el entrenamiento que corresponda la día de hoy
                            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                            // Le restamos 2 para que coincida con la id de cada entrenamiento
                            int dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2;
                            // Si es -1 significa que es domingo y por lo tanto debe de ser el
                            // último día de la semana no el primero
                            if (dayOfTheWeek == (-1)) { dayOfTheWeek = 6; }

                            for (Workout workout : routine.getWorkouts())
                            {
                                if (workout.getId() == dayOfTheWeek) { todayWorkout = workout; }
                            }

                            setView(true, view);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
                else { setView(false, view); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setView(boolean hasRoutine, View view)
    {
        if (hasRoutine)
        {
            View workoutList = view.findViewById(R.id.fragmentRecyclerView);

            loading.setVisibility(View.GONE);
            workoutProperties.setVisibility(View.VISIBLE);

            workoutDay.setText(todayWorkout.getDay());
            workoutName.setText(todayWorkout.getName());
            routineName.setText(routine.getName());
            // Set the adapter
            if (workoutList instanceof RecyclerView)
            {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) workoutList;
                adapter = new WorkoutAdapter(todayWorkout.getExercises());
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(adapter);
            }

            topAppBar.getMenu().getItem(0).setEnabled(true);
            topAppBar.getMenu().getItem(1).setEnabled(true);
        }
        else { loading.setVisibility(View.GONE); hint.setVisibility(View.VISIBLE); }
    }

    public void updateData() { adapter.notifyDataSetChanged(); }
}
