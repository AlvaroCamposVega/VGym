package com.alvaro.vgym.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvaro.vgym.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomNavigation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomNavigation extends Fragment
{
    // Required empty public constructor
    public BottomNavigation() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BottomNavigation.
     */
    public static BottomNavigation newInstance()
    {
        BottomNavigation fragment = new BottomNavigation();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {   // Inflate the layout for this fragment
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
           switch (menuItem.getItemId())
           {
               case R.id.mnuBottomNavRoutine:
                   Log.i("VGym", "Click en Rutina!");
                   break;
               case R.id.mnuBottomNavProfile:
                   Log.i("VGym", "Click en Perfil!");
                   break;
               case R.id.mnuBottomNavNewRoutine:
                   Log.i("VGym", "Click en Nueva Rutina!");
                   break;
           }

           return true;
        });
    }

    /**
     * Sets the index of the bottom navigation.
     * @param id The id.
     */
    public void setIndex(int id)
    {
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.fragmentBottomNavigation);
        bottomNav.setSelectedItemId(id);
    }
}
