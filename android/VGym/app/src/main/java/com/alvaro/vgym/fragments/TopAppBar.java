package com.alvaro.vgym.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvaro.vgym.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopAppBar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopAppBar extends Fragment
{
    private static final String ARG_CONFIG = "config";

    private int config;

    private FirebaseAuth fbAuth;

    // Required empty public constructor
    public TopAppBar() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param config Configuration of the top app bar.
     * @return A new instance of fragment TopAppBar.
     */
    public static TopAppBar newInstance(int config)
    {
        TopAppBar fragment = new TopAppBar();
        Bundle args = new Bundle();
        args.putInt(ARG_CONFIG, config);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Obtenemos los argumentos que se han pasado al fragment
        if (getArguments() != null) { config = getArguments().getInt(ARG_CONFIG); }
        // Obtener instancia de FirebaseAuth
        fbAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {   // Inflate the layout for this fragment
        if (config == 0)
        {   // Layout para la configuración 0 (pantalla principal)
            return inflater.inflate(
                R.layout.fragment_main_top_app_bar,
                container,
                false
            );

        } else
        {   // Layout para la configuración 1 (pantalla del perfil)
            return inflater.inflate(
                R.layout.fragment_profile_top_app_bar,
                container,
                false
            );
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // Obtenemos la barra superior de la vista
        MaterialToolbar topAppBar = getActivity().findViewById(R.id.fragmentTopAppBar);
        // Si la configuración es 0 (pantalla principal)
        if (config == 0)
        {   // Listener del menú de la barra de arriba para la configuración 0 (pantalla principal)
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
        // Si la configuración es 1 (pantalla del perfil)
        } else
        {
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
    }
}
