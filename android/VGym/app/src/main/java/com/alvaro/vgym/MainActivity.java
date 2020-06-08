package com.alvaro.vgym;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.alvaro.vgym.fragments.BottomNavigationFragment;
import com.alvaro.vgym.fragments.EditWorkoutFragment;
import com.alvaro.vgym.fragments.ExerciseDialogFragment;
import com.alvaro.vgym.fragments.RoutineDialogFragment;
import com.alvaro.vgym.fragments.WorkoutFragment;
import com.alvaro.vgym.fragments.WorkoutListFragment;
import com.alvaro.vgym.model.Exercise;
import com.alvaro.vgym.model.Workout;

public class MainActivity extends AppCompatActivity implements
    WorkoutListFragment.OnWorkoutSelectedListener,
    EditWorkoutFragment.OnExerciseSelectedListener
{
    private BottomNavigationFragment bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    public void onCopyWorkoutSelected(int id)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        WorkoutListFragment workoutListFragment = (WorkoutListFragment) fragmentManager
            .findFragmentByTag(RoutineDialogFragment.TAG).getChildFragmentManager()
            .findFragmentByTag(WorkoutListFragment.TAG);

        workoutListFragment.copyWorkout(id);
    }

    @Override
    public void onClearWorkoutSelected(int id)
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
    public void onBackPressed()
    {
        /*int index = bottomNavFragment.getIndex();

        switch (index)
        {
            case R.id.mnuBottomNavRoutine:
                finishAffinity();
                break;
            case R.id.mnuBottomNavNewRoutine:
                break;
            case R.id.mnuBottomNavProfile:
                break;
            default:
                super.onBackPressed();
        }*/

        super.onBackPressed();
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
            .replace(R.id.mainWorkoutFragment, workoutFragment, WorkoutFragment.TAG)
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
