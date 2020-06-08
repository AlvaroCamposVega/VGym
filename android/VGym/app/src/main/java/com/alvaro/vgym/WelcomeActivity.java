package com.alvaro.vgym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alvaro.vgym.services.FirebaseService;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity
{
    @BindView(R.id.welcomeLoginBtn)
    Button loginBtn;

    @BindView(R.id.welcomeContinueOfflineBtn)
    Button continueBtn;

    @BindView(R.id.welcomeRegisterLink)
    TextView registerLink;

    @BindView(R.id.welcomeUnsplashAttribution)
    TextView unsplashAttribution;

    private FirebaseService fbService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // Enlazar la actividad con ButterKnife
        ButterKnife.bind(this);
        // Obtenemos la instancia de FirebaseAuth
        fbService = FirebaseService.getInstance();
        // El botón de login lleva a la pantalla de login
        loginBtn.setOnClickListener(v -> {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        });
        // El botón de continuar offline lleva a la pantalla principal
        continueBtn.setOnClickListener(v -> {
            Snackbar.make(
                loginBtn,
                R.string.offline_not_implemented,
                Snackbar.LENGTH_SHORT
            ).show();

            /*Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);*/
        });
        // HACER QUE EL LINK DE REGISTRO NOS LLEVE A LA PANTALLA DE REGISTRO
        String registerLinkText = registerLink.getText().toString();
        // Hacemos un SpannableString para poder añadir spans con listeners al string
        SpannableString ss = new SpannableString(registerLinkText);
        // Guardamos el contexto en una variable ya que lo vamos a necesitar en el listener
        Context ctx = this;
        // Creamos un Span clickeable con un listener
        ClickableSpan clickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick(@NonNull View widget)
            {
              Intent registerIntent = new Intent(ctx, RegisterActivity.class);
              startActivity(registerIntent);
            }
        };
        // Si el texto empieza por N significa que está en inglés y dependiendo del idioma el inicio
        // y el fin de dónde ponemos el span varía
        if (registerLinkText.startsWith("N"))
        {   // Introducimos el Span clickeable en el SpannableString
            ss.setSpan(clickableSpan, 13, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        } else
        {
            ss.setSpan(clickableSpan, 16, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        // Finalmente introducimos el nuevo texto en el TextView
        registerLink.setText(ss);
        // Si no hacemos este paso no se le podrá hacer click al texto
        registerLink.setMovementMethod(LinkMovementMethod.getInstance());
        // Lo mismo para el link de la attribución el cual tiene una url definida
        unsplashAttribution.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Obtenemos el usuario que tenga una sesión iniciada en el dispositivo
        FirebaseUser currentUser = fbService.getCurrentUser();
        // Si tiene una sesión iniciada vamos directamente a la pantalla principal
        if (currentUser != null)
        {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
    }
}
