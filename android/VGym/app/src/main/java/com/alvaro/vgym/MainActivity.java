package com.alvaro.vgym;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.alvaro.vgym.fragments.BottomNavigation;
import com.alvaro.vgym.fragments.TopAppBar;
import com.alvaro.vgym.fragments.WorkoutDayFragment;
import com.alvaro.vgym.fragments.WorkoutFragment;
import com.alvaro.vgym.fragments.dummy.DummyContent;

public class MainActivity extends AppCompatActivity
    implements WorkoutFragment.OnListFragmentInteractionListener,
    WorkoutDayFragment.OnListFragmentInteractionListener
{
    private BottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Inicializamos los fragmentos iniciales
        initFragments();
    }


    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item)
    {

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
        // Obtenemos el fragmento de la barra superior
        TopAppBar topAppBar = TopAppBar.newInstance(R.layout.fragment_main_top_app_bar);
        // Obtenemos el fragmento de la barra de navegación inferior
        bottomNavigation = BottomNavigation.newInstance();
        // Añadimos los fragmentos a la actividad
        fragmentManager.beginTransaction()
            .replace(R.id.mainTopAppBar, topAppBar, TopAppBar.TAG)
            .replace(R.id.mainBottomNavigation, bottomNavigation, BottomNavigation.TAG)
            .commit();
    }
}
