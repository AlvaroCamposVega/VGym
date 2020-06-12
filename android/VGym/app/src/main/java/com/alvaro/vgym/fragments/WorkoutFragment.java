package com.alvaro.vgym.fragments;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alvaro.vgym.AlertReceiver;
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

import static com.alvaro.vgym.BaseApp.CHANNEL_ID;

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

    private View view;
    private TextView loading;
    private TextView hint;
    private ConstraintLayout workoutProperties;
    private LinearLayout restMsg;

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
        view = inflater.inflate(R.layout.fragment_workout_layout, container, false);

        topAppBar = view.findViewById(R.id.fragmentTopAppBar);
        setTopAppBarListener();

        loading = view.findViewById(R.id.workoutLoading);
        hint = view.findViewById(R.id.workoutHint);
        workoutProperties = view.findViewById(R.id.workoutProperties);
        restMsg = view.findViewById(R.id.workoutRestMsg);

        workoutDay = view.findViewById(R.id.workoutDay);
        workoutName = view.findViewById(R.id.workoutName);
        routineName = view.findViewById(R.id.workoutRoutineName);

        getData();

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
                    String uid = firebaseService.getUid();
                    String key = routine.getId();

                    routine.setFavorite(!routine.isFavorite());

                    firebaseService.save("routines/" + uid + "/" + key, routine);

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

    public void getData()
    {
        String uid = firebaseService.getUid();

        DatabaseReference dbSelectedRef = firebaseService.getReference("selected/" + uid);
        // Si el usuario no tiene una rutina seleccionada significa que no tiene ninguna rutina ya
        // que es obligatorio tener una seleccionada
        dbSelectedRef.addValueEventListener(new ValueEventListener()
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
                    dbRef.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            routine = dataSnapshot.getValue(Routine.class);

                            if (routine != null)
                            {   // De la rutina mostramos el entrenamiento que corresponda al día
                                // de hoy
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

                                setView(true);
                            } else { setView(false); }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
                else { setView(false); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();

        String uid = firebaseService.getUid();

        DatabaseReference dbSelectedRef = firebaseService.getReference("selected/" + uid);

        dbSelectedRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String selectedKey = dataSnapshot.getValue(String.class);

                if (selectedKey != null)
                {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, 8);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);

                    if (c.before(Calendar.getInstance())) { c.add(Calendar.DATE, 1); }

                    AlarmManager alarmManager = (AlarmManager) getActivity()
                        .getSystemService(Context.ALARM_SERVICE);

                    Intent intent = new Intent(getContext(), AlertReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getActivity(),
                        1,
                        intent,
                        0
                    );
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        c.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


    }

    private void setView(boolean hasRoutine)
    {
        if (hasRoutine)
        {
            loading.setVisibility(View.GONE);
            hint.setVisibility(View.GONE);
            workoutProperties.setVisibility(View.VISIBLE);

            workoutDay.setText(todayWorkout.getDay());

            if (todayWorkout.isRestDay())
            {
                restMsg.setVisibility(View.VISIBLE);
                workoutName.setVisibility(View.GONE);
                routineName.setVisibility(View.GONE);
            }
            else
            {
                View workoutList = view.findViewById(R.id.fragmentRecyclerView);

                restMsg.setVisibility(View.GONE);
                workoutName.setVisibility(View.VISIBLE);
                routineName.setVisibility(View.VISIBLE);

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
            }

            topAppBar.getMenu().getItem(0).setEnabled(true);
            topAppBar.getMenu().getItem(1).setEnabled(true);

            if (routine.isFavorite())
            {
                //String desc = getString(R.string.main_menu_del_favorites_desc);

                topAppBar.getMenu().getItem(1).setTitle(R.string.del_favorites);

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    topAppBar.getMenu().getItem(1).setContentDescription(desc);
                }*/
            }
            else
            {
                //String desc = getResources().getString(R.string.main_menu_add_favorites_desc);

                topAppBar.getMenu().getItem(1).setTitle(R.string.add_favorites);

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    topAppBar.getMenu().getItem(1).setContentDescription(desc);
                }*/
            }
        }
        else
        {
            loading.setVisibility(View.GONE);
            hint.setVisibility(View.VISIBLE);
            workoutProperties.setVisibility(View.VISIBLE);
        }
    }
}
