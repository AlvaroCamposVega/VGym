package com.alvaro.vgym;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.alvaro.vgym.fragments.BottomNavigation;
import com.alvaro.vgym.fragments.TopAppBar;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtenemos el fragment manager y comenzamos una transacción para añadir un fragmento
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Añadimos la barra superior con la configuración 0
        TopAppBar topAppBarFragment = TopAppBar.newInstance(0);
        fragmentTransaction.add(R.id.mainTopAppBarView, topAppBarFragment);
        fragmentTransaction.commit();
        // Obtenemos el fragmento de la barra de navegación inferior
        BottomNavigation bottomNavFragment = (BottomNavigation) getSupportFragmentManager()
            .findFragmentById(R.id.mainBottomNavigation);
        // Cambiamos el elemento seleccionado al de la rutina
        bottomNavFragment.setIndex(R.id.mnuBottomNavRoutine);
    }
}
