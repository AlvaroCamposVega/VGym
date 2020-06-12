package com.alvaro.vgym;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.alvaro.vgym.database.RoutineCacheContract;
import com.alvaro.vgym.fragments.BottomNavigationFragment;
import com.alvaro.vgym.fragments.EditWorkoutFragment;
import com.alvaro.vgym.fragments.ExerciseDialogFragment;
import com.alvaro.vgym.fragments.ProfileFragment;
import com.alvaro.vgym.fragments.RoutineDialogFragment;
import com.alvaro.vgym.fragments.WorkoutFragment;
import com.alvaro.vgym.fragments.WorkoutListFragment;
import com.alvaro.vgym.model.Exercise;
import com.alvaro.vgym.model.Routine;
import com.alvaro.vgym.model.Workout;
import com.alvaro.vgym.services.FirebaseService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
    WorkoutListFragment.OnWorkoutSelectedListener,
    EditWorkoutFragment.OnExerciseSelectedListener,
    ProfileFragment.OnRoutineInteractionListener
{
    private BottomNavigationFragment bottomNavigation;

    private FirebaseService firebaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseService = FirebaseService.getInstance();
        // Inicializamos los fragmentos iniciales
        initFragments();
    }

    @Override
    public void onWorkoutSelected(Workout workout)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        RoutineDialogFragment routineDialog = (RoutineDialogFragment) fragmentManager
            .findFragmentByTag(RoutineDialogFragment.TAG);

        routineDialog.getChildFragmentManager().beginTransaction()
            .setCustomAnimations(
                R.anim.slide_left_in,
                R.anim.fragment_fade_out,
                R.anim.slide_right_in,
                R.anim.fragment_fade_out
            )
            .replace(
                R.id.fragmentRoutineDialogContainer,
                EditWorkoutFragment.newInstance(workout),
                EditWorkoutFragment.TAG
            )
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onCopyWorkout(int id)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        WorkoutListFragment workoutListFragment = (WorkoutListFragment) fragmentManager
            .findFragmentByTag(RoutineDialogFragment.TAG).getChildFragmentManager()
            .findFragmentByTag(WorkoutListFragment.TAG);

        workoutListFragment.copyWorkout(id);
    }

    @Override
    public void onClearWorkout(int id)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        WorkoutListFragment workoutListFragment = (WorkoutListFragment) fragmentManager
            .findFragmentByTag(RoutineDialogFragment.TAG).getChildFragmentManager()
            .findFragmentByTag(WorkoutListFragment.TAG);

        workoutListFragment.resetWorkout(id);
    }

    @Override
    public void onExerciseSelected(Exercise exercise, boolean enabled)
    {
        if (enabled)
        {
            getEditWorkoutFragment().disableDialog();

            ExerciseDialogFragment exerciseDialog = ExerciseDialogFragment.newInstance(exercise);

            FragmentManager fragmentManager = getSupportFragmentManager();

            exerciseDialog.show(fragmentManager, ExerciseDialogFragment.TAG);
        }
    }

    @Override
    public void onDeleteExercise(Exercise exercise, boolean enabled)
    {
        if (enabled) { getEditWorkoutFragment().removeExercise(exercise); }
    }

    @Override
    public void onEditRoutine(Routine routine)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        RoutineDialogFragment routineDialog = RoutineDialogFragment.newInstance(routine);

        routineDialog.show(fragmentManager, RoutineDialogFragment.TAG);
    }

    @Override
    public void onAddRoutineToFavorites(Routine routine)
    {
        String uid = firebaseService.getUid();
        String key = routine.getId();

        routine.setFavorite(!routine.isFavorite());

        firebaseService.save("routines/" + uid + "/" + key, routine);
    }

    @Override
    public void onChangeSelectedRoutine(Routine routine)
    {
        String uid = firebaseService.getUid();

        DatabaseReference dbSelectedRef = firebaseService.getReference("selected/" + uid);

        dbSelectedRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String selectedRoutineKey = dataSnapshot.getValue(String.class);
                String reference = "routines/" + uid + "/" + selectedRoutineKey;

                DatabaseReference dbSelectedRoutineRef = firebaseService.getReference(reference);

                dbSelectedRoutineRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        Routine selectedRoutine = dataSnapshot.getValue(Routine.class);

                        selectedRoutine.setSelected(false);
                        routine.setSelected(true);

                        String key = routine.getId();

                        firebaseService.save("selected/" + uid, key);

                        String oldSelectedRoutineRef = "routines/" + uid + "/" + selectedRoutineKey;
                        String newSelectedRoutineRef = "routines/" + uid + "/" + key;

                        firebaseService.save(oldSelectedRoutineRef, selectedRoutine);
                        firebaseService.save(newSelectedRoutineRef, routine);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public void onDeleteRoutine(String id, List<Routine> routines)
    {   // Creamos el diálogo de alerta avisando de que se va a eliminar una rutina
        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.caution)
            .setMessage(R.string.delete_routine_confirmation_text)
            .setNeutralButton(R.string.cancel_label, null)
            .setPositiveButton(R.string.ok, (dialog, which) -> {
                // Obtenemos la rutina que vamos a eliminar
                Routine routineToDelete = null;

                for (Routine routine : routines)
                {
                    if (routine.getId().equals(id)) { routineToDelete = routine; }
                }
                // Obtenemos el UID y la referencia a la rutina para eliminarla
                String uid = firebaseService.getUid();
                String deleteRef = "routines/" + uid + "/" + id;
                // Si hay más de una rutina en la lista de rutinas contemplamos la posibilidad de
                // que se elimine una rutina no seleccionada o una que sí está seleccionada
                if (routines.size() > 1)
                {   // Si está seleccionada se pedirá al usuario que seleccione una nueva cuando
                    // Se elimine la anterior
                    if (routineToDelete.isSelected())
                    {
                        routines.remove(routineToDelete);

                        CharSequence[] items = new CharSequence[routines.size()];

                        for (int i = 0; i < items.length; i++)
                        {
                            items[i] = routines.get(i).getName();
                        }

                        DatabaseReference dbDeleteRef = firebaseService.getReference(deleteRef);
                        // Eliminamos la rutina y pedimos al usuario que seleccione una nueva a
                        // través de otro diálogo
                        dbDeleteRef.removeValue();

                        new MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.select_new_default_routine)
                            .setItems(items, (simpleDialog, chosenItem) -> {
                                // Cuando seleccione la nueva rutina actualizamos los valores en
                                // las referencias de selección y el booleano de la rutina
                                Routine selectedRoutine = routines.get(chosenItem);
                                selectedRoutine.setSelected(true);

                                String key = selectedRoutine.getId();
                                String selectedRoutineRef = "routines/" + uid + "/" + key;

                                firebaseService.save("selected/" + uid, key);
                                firebaseService.save(selectedRoutineRef, selectedRoutine);
                            })
                            .show();
                    } // Si la rutina no está seleccionada simplemente la eliminamos
                    else
                    {
                        DatabaseReference dbDeleteRef = firebaseService.getReference(deleteRef);

                        dbDeleteRef.removeValue();
                    }
                } // Si hay 1 rutina en la lista de rutinas la eliminamos y limpiamos la referencia
                else
                {
                    String selectedRef = "selected/" + uid;

                    DatabaseReference dbDeleteRef = firebaseService.getReference(deleteRef);
                    DatabaseReference dbSelectedRef = firebaseService.getReference(selectedRef);

                    dbDeleteRef.removeValue();
                    dbSelectedRef.removeValue();
                }
            })
            .show();
    }

    @Override
    public void onBackPressed()
    {
        int index = bottomNavigation.getIndex();

        switch (index)
        {
            case R.id.mnuBottomNavRoutine:
                finishAffinity();

                break;
            default:
                super.onBackPressed();
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        RoutineCacheContract.RoutineCacheDbHelper dbHelper = new RoutineCacheContract
            .RoutineCacheDbHelper(this);

        dbHelper.close();

        super.onDestroy();
    }

    private void initFragments()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Obtenemos el fragmento del entrenamiento
        WorkoutFragment workoutFragment = WorkoutFragment.newInstance();
        // Obtenemos el fragmento de la barra de navegación inferior
        bottomNavigation = BottomNavigationFragment.newInstance();
        // Añadimos los fragmentos a la actividad
        fragmentManager.beginTransaction()
            .replace(R.id.mainFragmentFrame, workoutFragment, WorkoutFragment.TAG)
            .replace(R.id.mainBottomNavigation, bottomNavigation, BottomNavigationFragment.TAG)
            .commit();
    }

    private EditWorkoutFragment getEditWorkoutFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        RoutineDialogFragment routineDialogFragment = (RoutineDialogFragment)
            fragmentManager.findFragmentByTag(RoutineDialogFragment.TAG);

        EditWorkoutFragment editWorkoutFragment = (EditWorkoutFragment) routineDialogFragment
            .getChildFragmentManager().findFragmentByTag(EditWorkoutFragment.TAG);

        return editWorkoutFragment;
    }
}
