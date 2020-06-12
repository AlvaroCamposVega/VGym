package com.alvaro.vgym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import com.alvaro.vgym.model.User;
import com.alvaro.vgym.services.FirebaseService;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity
{
    @BindView(R.id.registerEmailField)
    TextInputEditText emailField;

    @BindView(R.id.registerNameField)
    TextInputEditText nameField;

    @BindView(R.id.registerSurnameField)
    TextInputEditText surnameField;

    @BindView(R.id.registerPasswordField)
    TextInputEditText passwordField;

    @BindView(R.id.registerRepeatPasswordLayout)
    TextInputLayout repeatPasswordLayout;

    @BindView(R.id.registerRepeatPasswordField)
    TextInputEditText repeatPasswordField;

    @BindView(R.id.registerRegisterBtn)
    Button registerBtn;

    @BindView(R.id.registerUnsplashAttribution)
    TextView unsplashAttribution;

    private FirebaseService fbService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Enlazar la actividad con ButterKnife
        ButterKnife.bind(this);
        // Listener para indicar si las contraseñas coinciden o no
        addTextChangedListener(passwordField);
        addTextChangedListener(repeatPasswordField);

        registerBtn.setOnClickListener(v -> {
            // Obtenemos todos los campos
            String email = emailField.getText().toString();
            String name = nameField.getText().toString();
            String surname = surnameField.getText().toString();
            String password = passwordField.getText().toString();
            String repeatPassword = repeatPasswordField.getText().toString();
            // Si algún campo está vacío
            if (email.trim().isEmpty() || name.trim().isEmpty() || surname.trim().isEmpty()
                || password.trim().isEmpty() || repeatPassword.trim().isEmpty()
            )
            {
                Snackbar.make(
                    registerBtn,
                    R.string.fields_empty,
                    Snackbar.LENGTH_SHORT
                ).show();
            } // Si las contraseñas coinciden y los campos están rellenos se realiza el registro
            else if (!password.equals(repeatPassword))
            {
                repeatPasswordLayout.setError(getString(R.string.password_not_equal));
            }
            else
            {
                fbService = FirebaseService.getInstance();
                createAccountAndUser(email, password, name, surname);
            }
        });
        // Hacer el link del TextView de la atribución Clickable
        unsplashAttribution.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Añade el listener para determinar si las contraseñas coinciden o no.
     *
     * @param passwordEditText El campo de la contraseña.
     */
    private void addTextChangedListener(TextInputEditText passwordEditText)
    {
        passwordEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s)
            {
                String password = passwordField.getText().toString();
                String repeatPassword = repeatPasswordField.getText().toString();

                if (!password.equals(repeatPassword))
                {
                    repeatPasswordLayout.setError(getString(R.string.password_not_equal));
                }
                else { repeatPasswordLayout.setError(null); }
            }
        });
    }

    /**
     * Crea una autenticación en Firebase con el email y contraseña proporcionados y añade los datos
     * de este a la base de datos.
     * @param email El email.
     * @param password La contraseña.
     * @param name El nombre.
     * @param surname Los apellidos.
     */
    private void createAccountAndUser(String email, String password, String name, String surname)
    {   // Creamos la cuenta con FirebaseAuth
        fbService.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
            this, task -> {
                // Si la cuenta no se crea correctamente
                if (!task.isSuccessful())
                {
                    Snackbar.make(
                        registerBtn,
                        R.string.create_fbAccount_error,
                        Snackbar.LENGTH_SHORT
                    ).show();
                } // Si se crea correctamente añadimos los datos del usuario a la base de datos
                else
                {   // Creamos un objeto del usuario
                    User user = new User(name, surname);
                    String uid = fbService.getUid();
                    // Añadimos los datos del usuario a la base de datos
                    fbService.save("users/" + uid, user);
                    fbService.signOut();
                    // Lanzamos la actividad principal
                    Intent mainIntent = new Intent(this, LoginActivity.class);
                    startActivity(mainIntent);
                }
            }
        );
    }
}
