package com.alvaro.vgym.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alvaro.vgym.R;
import com.alvaro.vgym.model.Workout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Una simple subclase {@link Fragment}.
 * Usa el método {@link TopAppBar#newInstance} para crear
 * una instancia de este fragmento.
 */
public class TopAppBar extends Fragment
{
    public static final String TAG = "topAppBar";
    private static final String ARG_LAYOUT_ID = "layoutId";

    private int layoutId;
    private MaterialToolbar topAppBar;

    private FirebaseAuth fbAuth;

    // Constructor público vacío requerido.
    public TopAppBar() { }

    /**
     * Usa este método para crear una nueva instancia de
     * este fragmento usando los parámetros proporcionados.
     *
     * @param layoutId Id del layout para la top app bar.
     * @return Una nueva instancia de fragment TopAppBar.
     */
    public static TopAppBar newInstance(int layoutId)
    {
        TopAppBar fragment = new TopAppBar();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_ID, layoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Obtenemos los argumentos que se han pasado al fragment
        if (getArguments() != null) { layoutId = getArguments().getInt(ARG_LAYOUT_ID); }
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {   // Inflar el layout para este fragmento
        View view = inflater.inflate(layoutId, container, false);

        topAppBar = view.findViewById(R.id.fragmentTopAppBar);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // Obtenemos el Fragment Manager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        switch (layoutId)
        {   // Si la layout es la de la pantalla principal
            case R.layout.fragment_main_top_app_bar:
                // Reemplazamos el contenido por el fragmento que controla la rutina
                fragmentManager.beginTransaction().replace(
                    R.id.fragmentMainTopAppBarContent,
                    WorkoutFragment.newInstance(1),
                    WorkoutFragment.TAG
                ).commit();

                setMainTopAppBarListener();
                break;
            // Si la layout es la de la pantalla de perfil
            case R.layout.fragment_profile_top_app_bar:
                setProfileTopAppBarListener();
                break;
            // Si la layout es la del diálogo de nueva rutina
            case R.layout.fragment_new_routine_top_app_bar:
                FragmentManager childFragmentManager = this.getChildFragmentManager();

                childFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragmentNRTopAppBarContent,
                        NewRoutineName.newInstance(),
                        NewRoutineName.TAG
                    )
                    .commit();

                setNewRoutineTopAppBarListener();
                break;
        }
    }

    /**
     * Establece el listener de la top app bar para la pantalla principal.
     */
    private void setMainTopAppBarListener()
    {
        // Obtener instancia de FirebaseAuth
        fbAuth = FirebaseAuth.getInstance();
        // Listener
        topAppBar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId())
            {
                case R.id.mnuMainEditRoutine:
                    Log.i("VGym", "Click en Editar Rutina!");
                    break;
                case R.id.mnuMainAddToFavorites:
                    Log.i("VGym", "Click en Añadir a Favoritos!");
                    break;
                case R.id.mnuMainLogOut:
                    // Cerramos la sesión
                    fbAuth.signOut();
                    getActivity().finish(); // Finalizamos la actividad
                    break;
            }

            return false;
        });
    }

    /**
     * Establece el listener de la top app bar para la pantalla perfil.
     */
    private void setProfileTopAppBarListener()
    {
        // Obtener instancia de FirebaseAuth
        fbAuth = FirebaseAuth.getInstance();
        // Listener
        topAppBar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId())
            {
                case R.id.mnuProfileDeleteAccount:
                    Log.i("VGym", "Click en Eliminar Cuenta!");
                    break;
                case R.id.mnuProfileLogOut:
                    fbAuth.signOut();
                    getActivity().finish();
                    break;
            }

            return false;
        });
    }

    /**
     * Sustituye la top app bar actual a la del diálogo de nueva rutina.
     */
    private void swapToNewRoutineDialogTopAppBar()
    {
        MenuItem menuItem = topAppBar.getMenu().getItem(0);

        topAppBar.setTitle(R.string.label_new_routine);
        topAppBar.setNavigationIcon(R.drawable.ic_close_black_24dp);

        menuItem.setTitle(R.string.next);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String desc = getString(R.string.new_routine_menu_next_desc);
            menuItem.setContentDescription(desc);
        }

        setNewRoutineTopAppBarListener();
    }

    /**
     * Establece el listener de la top app bar para el nombre de la nueva rutina.
     */
    private void setNewRoutineTopAppBarListener()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        // Obtenemos el fragmento del diálogo de nueva rutina
        NewRoutineDialogFragment newRoutineDialogFragment = (NewRoutineDialogFragment)
            fragmentManager.findFragmentByTag(NewRoutineDialogFragment.TAG);
        // Listener del botón de cancelar (la X)
        topAppBar.setNavigationOnClickListener(v -> newRoutineDialogFragment.dismiss());
        // Listener del texto SIGUIENTE
        topAppBar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.mnuNewRoutineAction)
            {   // Si no hay errores en el nombre de la rutina
                if (newRoutineDialogFragment.setRoutineNameFromTextField())
                {
                    List<Workout> workouts = newRoutineDialogFragment.getRoutine().getWorkouts();
                    // Reemplazamos el contenido por el fragmento que controla los días de la rutina
                    FragmentManager childFragmentManager = this.getChildFragmentManager();
                    childFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_left_in,
                            R.anim.slide_left_out,
                            R.anim.slide_right_in,
                            R.anim.slide_right_out
                        )
                        .replace(
                            R.id.fragmentNRTopAppBarContent,
                            WorkoutDayFragment.newInstance(workouts),
                            WorkoutDayFragment.TAG
                        )
                        .addToBackStack(null)
                        .commit();
                    // Actualizamos la top app bar
                    swapToWorkoutDayTopAppBar(newRoutineDialogFragment);
                }// Si hay errores en el nombre mandamos una alerta al usuario
                else
                {
                    Snackbar.make(topAppBar, R.string.fields_empty, Snackbar.LENGTH_SHORT).show();
                }
            }

            return false;
        });
    }

    /**
     * Sustituye la top app bar actual a la de la lista de días.
     *
     * @param dialogFragment El fragmento del diálogo.
     */
    private void swapToWorkoutDayTopAppBar(NewRoutineDialogFragment dialogFragment)
    {
        MenuItem menuItem = topAppBar.getMenu().getItem(0);

        topAppBar.setTitle(dialogFragment.getRoutineName());
        topAppBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        menuItem.setTitle(R.string.create).setVisible(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String desc = getString(R.string.new_routine_menu_create_desc);
            menuItem.setContentDescription(desc);
        }

        setWorkoutDayTopAppBarListener();
    }

    /**
     * Establece el listener de la top app bar para la lista de días de la nueva rutina.
     */
    private void setWorkoutDayTopAppBarListener()
    {   // Listener del botón de navegar a la pantalla anterior
        topAppBar.setNavigationOnClickListener(v -> {
            // Retroceder a la pantalla de nombre de rutina
            this.getChildFragmentManager().popBackStack();
            // Actualizamos la top app bar
            swapToNewRoutineDialogTopAppBar();
        });
        // Listener del texto CREAR
        topAppBar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.mnuNewRoutineAction)
            {
                Log.i("VGym", "Crear Rutina!");
            }

            return false;
        });
    }

    /**
     * Sustituye la top app bar actual a la de la lista de ejercicios.
     *
     * @param exercisesFragment El fragmento de la lista de ejercicios.
     */
    public void swapToExercisesTopAppBar(ExercisesFragment exercisesFragment)
    {
        MenuItem menuItem = topAppBar.getMenu().getItem(0);

        topAppBar.setTitle(exercisesFragment.getWorkoutDay());

        menuItem.setVisible(false);

        setExercisesTopAppBarListener(exercisesFragment);
    }

    /**
     * Establece el listener de la top app bar para los ejercicios de la nueva rutina.
     */
    private void setExercisesTopAppBarListener(ExercisesFragment exercisesFragment)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        // Obtenemos el fragmento del diálogo de nueva rutina
        NewRoutineDialogFragment newRoutineDialogFragment = (NewRoutineDialogFragment)
            fragmentManager.findFragmentByTag(NewRoutineDialogFragment.TAG);
        // Listener del botón de navegar a la pantalla anterior
        topAppBar.setNavigationOnClickListener(v -> {
            // Establecemos el nombre del entrenamiento
            exercisesFragment.setExerciseNameFromTextField();
            // Retroceder a la pantalla de la lista de días
            this.getChildFragmentManager().popBackStack();
            // Actualizamos la top app bar
            swapToWorkoutDayTopAppBar(newRoutineDialogFragment);
        });
    }

    /**
     * Cambia a la pantalla de la lista de ejercicios del entrenamiento seleccionado.
     *
     * @param workout El entrenamiento.
     */
    public void workoutDaySelected(Workout workout)
    {
        FragmentManager childFragmentManager = this.getChildFragmentManager();

        childFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_left_in,
                R.anim.slide_left_out,
                R.anim.slide_right_in,
                R.anim.slide_right_out
            )
            .replace(
                R.id.fragmentNRTopAppBarContent,
                ExercisesFragment.newInstance(workout),
                ExercisesFragment.TAG
            )
            .addToBackStack(null)
            .commit();
    }
}
