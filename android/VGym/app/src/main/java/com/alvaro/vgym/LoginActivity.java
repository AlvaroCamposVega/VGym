package com.alvaro.vgym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity
{
    @BindView(R.id.loginEmailField)
    TextInputEditText emailField;

    @BindView(R.id.loginPasswordField)
    TextInputEditText passwordField;

    @BindView(R.id.loginLoginBtn)
    Button loginBtn;

    @BindView(R.id.loginUnsplashAttribution)
    TextView unsplashAttribution;

    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Enlazar la actividad con ButterKnife
        ButterKnife.bind(this);

        loginBtn.setOnClickListener(v ->
        {
            // Obtenemos el email y la contraseña
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            // Si algún campo está vacío
            if (email.trim().isEmpty() || password.trim().isEmpty())
            {
                Snackbar.make(
                    loginBtn,
                    R.string.fields_empty,
                    Snackbar.LENGTH_SHORT
                ).show();
            // Si todos los campos están rellenos se relaiza el inicio de sesión
            } else
            {
                // Obtenemos la instancia de Firebase Auth
                fbAuth = FirebaseAuth.getInstance();
                // Iniciamos sesión con los credenciales que ha introducido el ususario
                fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    this, task -> {
                        // Si el inicio de sesión falla
                        if (!task.isSuccessful())
                        {
                            // Indicamos al usuario que los credenciales son incorrectos
                            Snackbar.make(
                                loginBtn,
                                R.string.login_fail,
                                Snackbar.LENGTH_SHORT
                            ).show();

                        } else
                        {
                            Intent mainIntent = new Intent(this, MainActivity.class);
                            startActivity(mainIntent);
                        }
                    }
                );
            }
        });
        // Hacer el link del TextView de la atribución Clickable
        unsplashAttribution.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
