package com.alvaro.vgym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import com.alvaro.vgym.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private FirebaseAuth fbAuth;
    private FirebaseDatabase fbDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Enlazar la actividad con ButterKnife
        ButterKnife.bind(this);
        // Listener para indicar si las contraseñas coinciden o no
        passwordField.setOnFocusChangeListener((v, hasFocus) -> {
            // Si el input pierde el focus
            if (!hasFocus)
            {   // Obtenemos los Strings
                String password = passwordField.getText().toString();
                String repeatPassword = repeatPasswordField.getText().toString();
                // Si las contraseñas no coinciden ponemos un error en el input de repetir contraseña
                if (!password.equals(repeatPassword))
                {
                    repeatPasswordLayout.setError(getString(R.string.password_not_equal));
                // Si no lo limpiamos
                } else { repeatPasswordLayout.setError(null); }
            }
        });

        repeatPasswordField.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
            {
                String password = passwordField.getText().toString();
                String repeatPassword = repeatPasswordField.getText().toString();

                if (!password.equals(repeatPassword))
                {
                    repeatPasswordLayout.setError(getString(R.string.password_not_equal));

                } else { repeatPasswordLayout.setError(null); }
            }
        });

        registerBtn.setOnClickListener(v -> {
            // Obtenemos todos los campos
            String email = emailField.getText().toString();
            String name = nameField.getText().toString();
            String surname = surnameField.getText().toString();
            String password = passwordField.getText().toString();
            String repeatPassword = repeatPasswordField.getText().toString();
            // Si algún campos está vacío
            if (email.trim().isEmpty() || name.trim().isEmpty() || surname.trim().isEmpty()
                || password.trim().isEmpty() || repeatPassword.trim().isEmpty()
            )
            {
                Snackbar.make(
                    registerBtn,
                    R.string.fields_empty,
                    Snackbar.LENGTH_SHORT
                ).show();
            // Si las contraseñas coinciden y los campos están rellenos se realiza el registro
            } else if (repeatPasswordLayout.getError() == null)
            {
                fbAuth = FirebaseAuth.getInstance();
                createAccountAndUser(email, password, name, surname);
            }
        });
        // Hacer el link del TextView de la atribución Clickable
        unsplashAttribution.setMovementMethod(LinkMovementMethod.getInstance());
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
    {
        Context ctx = this;
        // Creamos la cuenta con FirebaseAuth
        fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
            this,
            task -> {
                // Si la cuenta no se crea correctamente
                if (!task.isSuccessful())
                {
                    Snackbar.make(
                        registerBtn,
                        R.string.create_fbAccount_error,
                        Snackbar.LENGTH_SHORT
                    );
                // Si se crea correctamente añadimos los datos del usuario a la base de datos
                } else
                {
                    String uid = fbAuth.getUid(); // UID del usuario
                    // Creamos un objeto del usuario
                    User user = new User(name, surname);
                    // Obtenemos instancia de la base de datos
                    fbDatabase = FirebaseDatabase.getInstance();
                    // Obtenemos referencia a la "tabla" de los usuarios
                    DatabaseReference dbRef = fbDatabase.getReference("users");
                    // Añadimos los datos del usuario a la base de datos
                    dbRef.child(uid).setValue(user);
                    // Lanzamos la actividad principal
                    Intent mainIntent = new Intent(ctx, MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        );
    }
}
