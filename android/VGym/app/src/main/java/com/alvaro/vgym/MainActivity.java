package com.alvaro.vgym;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
{
    @BindView(R.id.mainTopAppBar)
    MaterialToolbar topAppBar;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    private FirebaseAuth fbAuth;
    private FirebaseDatabase fbDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enlazar la actividad con ButterKnife
        ButterKnife.bind(this);
        // Listener del menú de la barra de arriba
        topAppBar.setOnMenuItemClickListener(menuItem -> {

            switch (menuItem.getItemId())
            {
                case R.id.mainMnuEditRoutine:
                    Log.i("VGym", "Click en Editar Rutina!");
                    break;
                case R.id.mainMnuAddToFavorites:
                    Log.i("VGym", "Click en Añadir a Favoritos!");
                    break;
                case R.id.mainMnuLogOut:
                    fbAuth = FirebaseAuth.getInstance();
                    // Cerramos la sesión
                    fbAuth.signOut();
                    finish(); // Finalizamos la actividad
                    break;
            }

            return false;
        });
        // Poner la navegación inferior en su correspondiente item
        bottomNavigation.setSelectedItemId(R.id.bottomNavRoutine);
    }
}
