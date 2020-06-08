package com.alvaro.vgym.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.alvaro.vgym.R;
import com.alvaro.vgym.model.Exercise;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExerciseDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseDialogFragment extends DialogFragment
{
    public static final String TAG = "newExerciseDialog";
    private static final String ARG_PARAM = "exercise";

    private Exercise exercise;
    private MaterialToolbar topAppBar;
    private boolean editMode;

    private TextInputEditText nameInput;
    private TextInputEditText repsInput;
    private TextInputEditText setsInput;
    // Required empty public constructor
    public ExerciseDialogFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param exercise El ejercicio.
     * @return A new instance of fragment NewExerciseDialogFragment.
     */
    public static ExerciseDialogFragment newInstance(Exercise exercise)
    {
        ExerciseDialogFragment fragment = new ExerciseDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, exercise);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);

        if (getArguments() != null)
        {
            exercise = (Exercise) getArguments().getSerializable(ARG_PARAM);
        }

        editMode = false;
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {   // Inflate the layout for this fragment
        View view = inflater.inflate(
            R.layout.fragment_exercise_dialog,
            container,
            false
        );

        topAppBar = view.findViewById(R.id.fragmentTopAppBar);
        setTopAppBarListener();
        nameInput = view.findViewById(R.id.ExerciseDialogName);
        repsInput = view.findViewById(R.id.ExerciseDialogReps);
        setsInput = view.findViewById(R.id.ExerciseDialogSets);

        nameInput.setText(exercise.getName());

        if (exercise.getReps() != 0) { repsInput.setText(String.valueOf(exercise.getReps())); }
        if (exercise.getSets() != 0) { setsInput.setText(String.valueOf(exercise.getSets())); }

        if (!exercise.getName().trim().isEmpty() && exercise.getReps() != 0
            && exercise.getSets() != 0
        )
        {
            editMode = true;
        }

        return view;
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

    @Override
    public void onDismiss(@NonNull DialogInterface dialog)
    {
        getEditWorkoutFragment().enableDialogAndUpdateData();
        super.onDismiss(dialog);
    }

    /**
     * Establece el listener de la top app bar para este fragmento.
     */
    private void setTopAppBarListener()
    {
        topAppBar.setNavigationOnClickListener(v -> dismiss());

        topAppBar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.mnuExerciseDialogOk) { validateExercise(); }

            return false;
        });
    }

    private void validateExercise()
    {
        String name = nameInput.getText().toString();
        String repsText = repsInput.getText().toString();
        String setsText = setsInput.getText().toString();

        int reps;
        int sets;

        if (name.trim().isEmpty() || repsText.trim().isEmpty() || setsText.trim().isEmpty())
        {
            Snackbar.make(
                nameInput,
                R.string.fields_empty,
                BaseTransientBottomBar.LENGTH_SHORT
            ).show();
        }
        else
        {
            reps = Integer.parseInt(repsText);
            sets = Integer.parseInt(setsText);

            if (reps == 0 || sets == 0)
            {
                Snackbar.make(
                    nameInput,
                    R.string.value_higher_than_zero,
                    BaseTransientBottomBar.LENGTH_SHORT
                ).show();
            }
            else
            {
                exercise.setName(name);
                exercise.setReps(reps);
                exercise.setSets(sets);

                if (!editMode) { getEditWorkoutFragment().addExercise(exercise); }

                dismiss();
            }
        }
    }

    private EditWorkoutFragment getEditWorkoutFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();

        RoutineDialogFragment routineDialogFragment = (RoutineDialogFragment)
            fragmentManager.findFragmentByTag(RoutineDialogFragment.TAG);

        EditWorkoutFragment editWorkoutFragment = (EditWorkoutFragment) routineDialogFragment
            .getChildFragmentManager().findFragmentByTag(EditWorkoutFragment.TAG);

        return editWorkoutFragment;
    }
}
