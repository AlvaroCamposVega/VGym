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
import android.widget.FrameLayout;

import com.alvaro.vgym.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Una simple subclase {@link Fragment}.
 * Usa el método {@link BottomNavigation#newInstance} para crear
 * una instancia de este fragmento.
 */
public class BottomNavigation extends Fragment
{
    public static final String TAG = "bottomNavigation";

    // Constructor público vacío requerido.
    public BottomNavigation() { }

    /**
     * Usa este método para crear una nueva instancia de
     * este fragmento usando los parámetros proporcionados.
     *
     * @return Una nueva instancia de fragment BottomNavigation.
     */
    public static BottomNavigation newInstance() { return new BottomNavigation(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {   // Inflar el layout para este fragmento
        return inflater.inflate(R.layout.fragment_bottom_navigation, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // Obtenemos la barra de navegación de la vista
        BottomNavigationView bottomNavigation = getActivity()
            .findViewById(R.id.fragmentBottomNavigation);
        // Listener para la barra de navegación inferior
        bottomNavigation.setOnNavigationItemSelectedListener(menuItem -> {
            boolean result = true;

            switch (menuItem.getItemId())
            {
                case R.id.mnuBottomNavRoutine:
                    // Quitar fragmento perfil y poner fragmento rutina
                    break;
                case R.id.mnuBottomNavProfile:
                    // Quitar fragmento rutina y poner fragmento perfil
                    Log.i("VGym", "Click en Perfil!");
                    break;
                case R.id.mnuBottomNavNewRoutine:
                    NewRoutineDialogFragment dialog = NewRoutineDialogFragment.newInstance();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    dialog.show(fragmentManager, NewRoutineDialogFragment.TAG);
                    result = false;
                    break;
            }

            return result;
        });
        // 'Sobreescribir' el listener para que si se reselecciona algún item no se tomen acciones.
        bottomNavigation.setOnNavigationItemReselectedListener(menuItem -> { });

        this.setIndex(R.id.mnuBottomNavRoutine);
    }

    /**
     * Establece el índice de la barra de navegación inferior.
     *
     * @param id El id.
     */
    public void setIndex(int id)
    {
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.fragmentBottomNavigation);
        bottomNav.setSelectedItemId(id);
    }

    /**
     * Devuelve el índice de la barra de navegación inferior.
     *
     * @return El índice.
     */
    public int getIndex()
    {
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.fragmentBottomNavigation);
        return bottomNav.getSelectedItemId();
    }
}
