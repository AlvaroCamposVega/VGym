package com.alvaro.vgym.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alvaro.vgym.R;
import com.alvaro.vgym.model.Routine;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutineDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutineDialogFragment extends DialogFragment
{
    public static final String TAG = "newRoutineDialog";
    private static final String ARG_PARAM = "routine";

    private Routine routine;
    // Required empty public constructor
    public RoutineDialogFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewRoutineDialogFragment.
     */
    public static RoutineDialogFragment newInstance(Routine routine)
    {
        RoutineDialogFragment fragment = new RoutineDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, routine);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Establecer el estilo del diálogo
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);

        if (getArguments() != null)
        {
            routine = (Routine) getArguments().getSerializable(ARG_PARAM);
        }

        FragmentManager childFragmentManager = this.getChildFragmentManager();
        // Instanciar el framento del nombre de la rutina
        childFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentRoutineDialogContainer,
                RoutineNameFragment.newInstance(routine),
                RoutineNameFragment.TAG
            )
            .commit();
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {   // Inflate the layout for this fragment
        View rootView = inflater.inflate(
            R.layout.fragment_routine_dialog,
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
    public void onStart()
    {
        super.onStart();

        /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        WorkoutFragment workoutFragment = (WorkoutFragment) fragmentManager
            .findFragmentByTag(WorkoutFragment.TAG);
        BottomNavigationFragment bottomNavigation = (BottomNavigationFragment) fragmentManager
            .findFragmentByTag(BottomNavigationFragment.TAG);
        // Añadimos los fragmentos a la actividad
        fragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .remove(workoutFragment)
            .remove(bottomNavigation)
            .commit();*/
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
}
