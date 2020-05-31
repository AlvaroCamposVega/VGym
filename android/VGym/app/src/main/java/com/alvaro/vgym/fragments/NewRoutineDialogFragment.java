package com.alvaro.vgym.fragments;

import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
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
import android.widget.FrameLayout;

import com.alvaro.vgym.R;
import com.alvaro.vgym.model.Routine;
import com.alvaro.vgym.model.Workout;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewRoutineDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewRoutineDialogFragment extends DialogFragment
{
    public static final String TAG = "newRoutineDialog";

    private Routine routine;
    FragmentManager childFragmentManager;

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

        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);

        childFragmentManager = this.getChildFragmentManager();
        childFragmentManager.beginTransaction()
            .replace(
                R.id.newRoutineTopAppBar,
                TopAppBar.newInstance(R.layout.fragment_new_routine_top_app_bar),
                TopAppBar.TAG
            )
            .commit();

        this.routine = new Routine(getContext());
    }

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
        /*switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
        {
            case Configuration.UI_MODE_NIGHT_YES:
                int color = getActivity().getColor(R.color.backgroundD);

                rootView.findViewById(R.id.newRoutineTopAppBar).setBackgroundColor(color);
                break;
        }*/

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimations);
        return dialog;
    }

    /**
     * Cambia a la pantalla de la lista de ejercicios del entrenamiento seleccionado.
     *
     * @param workout El entrenamiento.
     */
    public void workoutDaySelected(Workout workout)
    {   // Obtenemos la top app bar
        TopAppBar topAppBar = (TopAppBar) childFragmentManager.findFragmentByTag(TopAppBar.TAG);
        topAppBar.workoutDaySelected(workout);
    }

    /**
     * Establece el nombre de la rutina desde el campo nombre del fragmento nombre rutina.
     *
     * @return False si el campo está vacío.
     */
    public boolean setRoutineNameFromTextField()
    {   // Obtenemos la top app bar
        TopAppBar topAppBar = (TopAppBar) childFragmentManager.findFragmentByTag(TopAppBar.TAG);
        // Obtenemos el fragment manager hijo de la top app bar para obtener el fragmento del nombre
        // de la rutina
        NewRoutineName routineNameFragment = (NewRoutineName) topAppBar.getChildFragmentManager()
            .findFragmentByTag(NewRoutineName.TAG);

        boolean result = false;
        String name = routineNameFragment.getRoutineNameFromTextField();
        // Si el campo del nombre de la rutina no está vacío establecemos el nombre en el objeto
        // de la rutina y cambiamos el resultado a verdadero
        if (!name.trim().isEmpty()) { setRoutineName(name); result = true; }

        return result;
    }

    public Routine getRoutine() { return routine; }

    public String getRoutineName() { return this.routine.getName(); }

    public void setRoutineName(String name) { this.routine.setName(name); }
}
