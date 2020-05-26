package com.alvaro.vgym.fragments;

import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.alvaro.vgym.R;
import com.alvaro.vgym.model.Routine;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewRoutineDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewRoutineDialogFragment extends DialogFragment
{
    public static final String TAG = "newRoutineDialog";

    public Routine routine;

    // Required empty public constructor
    public NewRoutineDialogFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewRoutineDialogFragment.
     */
    public static NewRoutineDialogFragment newInstance() { return new NewRoutineDialogFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TopAppBar topAppBar = TopAppBar.newInstance(R.layout.fragment_new_routine_top_app_bar);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(
            R.id.newRoutineTopAppBar, topAppBar, TopAppBar.TAG
        ).commit();

        this.routine = new Routine();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {   // Inflate the layout for this fragment
        View rootView = inflater.inflate(
            R.layout.fragment_new_routine_dialog,
            container,
            false
        );
        // Si el tema activo es el tema oscuro cambiamos el color de fondo del fragmento
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
        {
            case Configuration.UI_MODE_NIGHT_YES:
                int color = getActivity().getColor(R.color.backgroundD);

                rootView.findViewById(R.id.newRoutineTopAppBar).setBackgroundColor(color);
                break;
        }

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /**
     * Establece el nombre de la rutina desde el campo nombre del fragmento nombre rutina.
     *
     * @return False si el campo está vacío.
     */
    public boolean setRoutineNameFromTextField()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        NewRoutineName routineNameFragment = (NewRoutineName) fragmentManager
            .findFragmentByTag(NewRoutineName.TAG);

        return routineNameFragment.setRoutineNameFromTextField();
    }
}
