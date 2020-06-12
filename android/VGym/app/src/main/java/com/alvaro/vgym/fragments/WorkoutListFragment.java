package com.alvaro.vgym.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alvaro.vgym.R;
import com.alvaro.vgym.adapters.WorkoutListAdapter;
import com.alvaro.vgym.model.Exercise;
import com.alvaro.vgym.model.Routine;
import com.alvaro.vgym.model.Workout;
import com.alvaro.vgym.services.FirebaseService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnWorkoutSelectedListener}
 * interface.
 */
public class WorkoutListFragment extends Fragment
{
    public static final String TAG = "workoutDayFragment";
    private static final String ARG_ROUTINE = "routine";

    private Routine routine;
    private MaterialToolbar topAppBar;
    private boolean editMode;

    private WorkoutListAdapter adapter;

    private OnWorkoutSelectedListener listener;

    private FirebaseService firebaseService;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkoutListFragment() { }

    @SuppressWarnings("unused")
    public static WorkoutListFragment newInstance(Routine routine)
    {
        WorkoutListFragment fragment = new WorkoutListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ROUTINE, routine);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            routine = (Routine) getArguments().getSerializable(ARG_ROUTINE);
        }

        editMode = false;
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {
        View view = inflater.inflate(
            R.layout.fragment_workout_list_layout,
            container,
            false
        );

        View list = view.findViewById(R.id.fragmentRecyclerView);

        topAppBar = view.findViewById(R.id.fragmentTopAppBar);
        topAppBar.setTitle(routine.getName());
        setTopAppBarListener();
        // Set the adapter
        if (list instanceof RecyclerView)
        {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) list;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            adapter = new WorkoutListAdapter(routine.getWorkouts(), listener);
            recyclerView.setAdapter(adapter);
        }
        // Si la id de la rutina no está vacía estamos en modo de edición
        if (!routine.getId().isEmpty()) { editMode = true; }

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof OnWorkoutSelectedListener)
        {
            listener = (OnWorkoutSelectedListener) context;

        } else
        {
            throw new RuntimeException(
                context.toString() + " must implement OnWorkoutSelectedListener"
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
     * Establece el listener de la top app bar para este fragmento.
     */
    private void setTopAppBarListener()
    {   // Listener del botón de navegar a la pantalla anterior
        topAppBar.setNavigationOnClickListener(v -> {
            // Retroceder a la pantalla de nombre de rutina
            getFragmentManager().popBackStack();
        });
        // Listener del texto CREAR
        topAppBar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.mnuWorkoutListCreate)
            {
                if (validateRoutine())
                {
                    clearRestWorkouts();

                    firebaseService = FirebaseService.getInstance();

                    final String uid = firebaseService.getUid();
                    // Si estamos en modo de edición actualizamos la rutina en la nube
                    if (editMode)
                    {
                        String key = routine.getId();

                        firebaseService.save("routines/" + uid + "/" + key, routine);
                    } // Si no estamos en modo de edición añadimos la nueva rutina a la nube
                    else
                    {   // Obtenemos la nueva Id/key para la nueva rutina
                        final String key = firebaseService.getNewReferenceKey("routines/" + uid);

                        routine.setId(key);
                        // Obtenemos la referencia a la tabla selected para determinar si es la
                        // primera rutina que crea el usuario
                        DatabaseReference dbSelectedRef = firebaseService.getReference(
                            "selected/" + uid
                        );
                        // Si es la primera rutina que crea el usuario la marcamos como seleccionada
                        dbSelectedRef.addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                String selectedRoutineKey = dataSnapshot.getValue(String.class);
                                // Si es la primera rutina la añadimos a la tabla selected
                                if (selectedRoutineKey == null)
                                {
                                    firebaseService.save("selected/" + uid, key);

                                    routine.setSelected(true);
                                }

                                firebaseService.save(
                                    "routines/" + uid + "/" + key, routine
                                );
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }

                    RoutineDialogFragment dialog = (RoutineDialogFragment) getParentFragment();

                    dialog.dismiss();
                }
            }

            return false;
        });
    }

    /**
     * Valida la rutina actual.
     *
     * @return True si la rutina es válida.
     */
    private boolean validateRoutine()
    {
        boolean routineCorrect = true;
        List<Workout> workouts = routine.getWorkouts();

        int i = 0;
        int size = routine.getWorkouts().size();

        while (i < size && routineCorrect)
        {
            Workout workout = workouts.get(i);

            if (!workout.isRestDay() && workout.getName().trim().isEmpty())
            {
                routineCorrect = false;

                Snackbar.make(
                    topAppBar,
                    R.string.error_workout_name_empty,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show();
            }
            else if (!workout.isRestDay() && workout.getExercises().isEmpty())
            {
                routineCorrect = false;

                Snackbar.make(
                    topAppBar,
                    R.string.error_exercises_list_empty,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show();
            }

            i++;
        }

        return routineCorrect;
    }

    /**
     * Limpia todos los entrenamientos que sean días de descanso para no guardar más información
     * de la necesaria en la nube.
     */
    private void clearRestWorkouts()
    {
        for (Workout workout : routine.getWorkouts())
        {
            if (workout.isRestDay())
            {
                workout.setName("");
                workout.setExercises(new ArrayList<Exercise>());
            }
        }
    }

    /**
     * Habilita la función de copiar un entrenamiento.
     *
     * @param id La id del entrenamiento.
     */
    public void copyWorkout(int id)
    {
        CharSequence[] workoutDays = new String[6]; // Array con los días de la semana
        Workout copyWorkout = null;

        int i = 0;
        // Añadimos los días de la semana (menos el que se va a copiar) al array y obtenemos el
        // entrenamiento que se va a copiar
        for (Workout workout : routine.getWorkouts())
        {
            if (workout.getId() != id) { workoutDays[i] = workout.getDay(); i++; }
            else { copyWorkout = workout; }
        }

        final Workout from = copyWorkout;
        Workout to = new Workout();
        // inicializamos la id a la del primer entrenamiento en la lista de entrenamientos
        to.setId(findWorkoutByDay((String) workoutDays[0]).getId());
        // Creamos el diálogo con las opciones
        new MaterialAlertDialogBuilder(getContext())
            .setTitle(R.string.copy_to_label)
            .setNeutralButton(R.string.cancel_label, null)
            .setPositiveButton(R.string.ok, (dialog, which) ->
                copyWorkoutToWorkout(from, to.getId())
            )
            .setSingleChoiceItems(workoutDays, 0, (dialog, which) ->
                to.setId((which >= id) ? (which + 1) : which)
            )
            .show();
    }

    /**
     * Devuelve un entrenamiento dado su día.
     *
     * @param day El día.
     * @return El entrenamiento con el día especificado.
     */
    private Workout findWorkoutByDay(String day)
    {
        Workout result = null;

        for (Workout workout : routine.getWorkouts())
        {
            if (workout.getDay().equals(day)) { result = workout; }
        }

        return result;
    }

    /**
     * Copia un entrenamiento en otro.
     *
     * @param from Entrenamiento desde el que se copia.
     * @param toId Entrenamiento en el que se copia.
     */
    private void copyWorkoutToWorkout(Workout from, int toId)
    {
        for (Workout workout : routine.getWorkouts())
        {
            if (workout.getId() == toId)
            {
                workout.setName(from.getName());
                workout.setRestDay(from.isRestDay());
                workout.copyExercises(from.getExercises());

                adapter.notifyItemChanged(toId);
            }
        }
    }

    /**
     * Reinicia el entrenamiento con la id especificada.
     *
     * @param id La id.
     */
    public void resetWorkout(int id)
    {
        for (Workout workout : routine.getWorkouts())
        {
            if (workout.getId() == id)
            {
                workout.setName("");
                workout.setRestDay(false);
                workout.setExercises(new ArrayList<Exercise>());

                adapter.notifyItemChanged(id);
            }
        }
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
    public interface OnWorkoutSelectedListener
    {
        void onWorkoutSelected(Workout workout);
        void onCopyWorkout(int id);
        void onClearWorkout(int id);
    }
}
