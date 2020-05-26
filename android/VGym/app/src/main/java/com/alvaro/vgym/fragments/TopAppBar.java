package com.alvaro.vgym.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvaro.vgym.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Una simple subclase {@link Fragment}.
 * Usa el método {@link TopAppBar#newInstance} para crear
 * una instancia de este fragmento.
 */
public class TopAppBar extends Fragment
{
    public static final String TAG = "topAppBar";
    private static final String ARG_LAYOUT_ID = "layoutId";
    private static final String WORKOUT_DAY_TOP_APP_BAR_BACK_STACK_NAME = "workoutDayBackStack";

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
            // Si la layout es la del modal de nueva rutina
            case R.layout.fragment_new_routine_top_app_bar:
                NewRoutineDialogFragment newRoutineDialogFragment = (NewRoutineDialogFragment)
                    fragmentManager.findFragmentByTag(NewRoutineDialogFragment.TAG);

                fragmentManager.beginTransaction().replace(
                    R.id.fragmentNRTopAppBarContent,
                    NewRoutineName.newInstance(),
                    NewRoutineName.TAG
                ).commit();

                setNewRoutineTopAppBarListener(newRoutineDialogFragment);
                break;
            // Si la layout es la del modal de la lista de días de la rutina
            case R.layout.fragment_workout_day_top_app_bar:
                // Obtenemos el fragmento de la nueva rutina
                newRoutineDialogFragment = (NewRoutineDialogFragment)
                    fragmentManager.findFragmentByTag(NewRoutineDialogFragment.TAG);
                // Cambiamos el título de la top app bar por el nombre de la rutina
                topAppBar.setTitle(newRoutineDialogFragment.routine.getName());
                // Reemplazamos el contenido por el fragmento que controla los días de la rutina
                fragmentManager.beginTransaction()
                    .replace(
                        R.id.fragmentWDTopAppBarContent,
                        WorkoutDayFragment.newInstance(1),
                        WorkoutDayFragment.TAG
                    ).addToBackStack(null).commit();

                setWorkoutDayTopAppBarListener();
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
     * Establece el listener de la top app bar para el nombre de la nueva rutina.
     */
    private void setNewRoutineTopAppBarListener(NewRoutineDialogFragment newRoutineDialogFragment)
    {   // Listener del botón de cancelar (la X)
        topAppBar.setNavigationOnClickListener(v -> {
            newRoutineDialogFragment.dismiss(); // Cerramos el modal
            // Volvemos a añadir la barra de navegación inferior a su correspondiente contenedor
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                .replace(
                    R.id.mainBottomNavigation,
                    BottomNavigation.newInstance(),
                    BottomNavigation.TAG
                ).commit();
        });
        // Listener del texto SIGUIENTE
        topAppBar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.mnuNewRoutineNext)
            {   // Si no hay errores en el nombre de la rutina
                if (newRoutineDialogFragment.setRoutineNameFromTextField())
                {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    // Reemplazamos la top app bar por la de la lista de días y la añadimos a la
                    // pila con un nombre para posteriormente poder revertir los cambios
                    fragmentManager.beginTransaction().replace(
                        R.id.newRoutineTopAppBar,
                        TopAppBar.newInstance(R.layout.fragment_workout_day_top_app_bar),
                        TopAppBar.TAG
                    ).addToBackStack(WORKOUT_DAY_TOP_APP_BAR_BACK_STACK_NAME).commit();
                // Si hay errores en el nombre mandamos una alerta al usuario
                } else
                {
                    Snackbar.make(topAppBar, R.string.fields_empty, Snackbar.LENGTH_SHORT).show();
                }
            }

            return false;
        });
    }

    /**
     * Establece el listener de la top app bar para la lista de días de la nueva rutina.
     */
    private void setWorkoutDayTopAppBarListener()
    {   // Listener del botón de navegar a la pantalla anterior
        topAppBar.setNavigationOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            // Retroceder a la pantalla de nombre de rutina
            fragmentManager.popBackStack(
                WORKOUT_DAY_TOP_APP_BAR_BACK_STACK_NAME,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            );
        });
        // Listener del texto CREAR
        topAppBar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.mnuWorkoutDayCreate)
            {
                Log.i("VGym", "Crear Rutina!");
            }

            return false;
        });
    }
}
