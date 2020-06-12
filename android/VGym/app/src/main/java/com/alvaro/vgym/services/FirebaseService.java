package com.alvaro.vgym.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class FirebaseService
{
    private static FirebaseService firebaseService;

    private FirebaseAuth fbAuth;
    private FirebaseDatabase fbDatabase;
    private DatabaseReference dbRef;

    private FirebaseService()
    {
        fbAuth = FirebaseAuth.getInstance();
        fbDatabase = FirebaseDatabase.getInstance();
        dbRef = fbDatabase.getReference();
    }

    public static FirebaseService getInstance()
    {
        if (firebaseService == null) { firebaseService = new FirebaseService(); }

        return firebaseService;
    }

    public FirebaseUser getCurrentUser() { return fbAuth.getCurrentUser(); }

    public String getUid() { return fbAuth.getUid(); }

    public String getEmail() { return getCurrentUser().getEmail(); }

    public Task<AuthResult> signInWithEmailAndPassword(String email, String password)
    {
        return fbAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> createUserWithEmailAndPassword(String email, String password)
    {
        return fbAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> deleteUser() { return fbAuth.getCurrentUser().delete(); }

    public void signOut() { fbAuth.signOut(); }

    /**
     * Obtener una nueva clave para la referencia especificada.
     *
     * @param reference La referencia a la base de datos.
     * @return Una nueva clave para la referencia especificada.
     */
    public String getNewReferenceKey(String reference)
    {
        return dbRef.child(reference).push().getKey();
    }

    public DatabaseReference getReference(String reference)
    {
        return fbDatabase.getReference(reference);
    }

    /**
     * Añade o actualiza un elemento en la nube.
     *
     * @param reference La referencia a la base de datos.
     * @param object El objeto.
     */
    public void save(String reference, Serializable object)
    {
        dbRef.child(reference).setValue(object);
    }

    public Task<Void> sendPasswordResetEmail(String email)
    {
        return fbAuth.sendPasswordResetEmail(email);
    }
}
