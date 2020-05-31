package com.alvaro.vgym.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alvaro.vgym.R;
import com.alvaro.vgym.adapters.ExerciseAdapter;
import com.alvaro.vgym.fragments.dummy.DummyContent;
import com.alvaro.vgym.fragments.dummy.DummyContent.DummyItem;
import com.alvaro.vgym.model.Exercise;
import com.alvaro.vgym.model.Workout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnExerciseSelectedListener}
 * interface.
 */
public class ExercisesFragment extends Fragment
{
    public static final String TAG = "exercisesFragment";
    private static final String ARG_WORKOUT = "workout";

    private Workout workout = null;
    private SwitchMaterial restSwitch;
    private TextInputLayout workoutNameEditTextLayout;
    private TextInputEditText workoutNameEditText;
    private ExtendedFloatingActionButton addBtn;
    private TextView hint;
    private View exercisesList;
    private LinearLayout restMsg;

    private OnExerciseSelectedListener listener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExercisesFragment() { }

    @SuppressWarnings("unused")
    public static ExercisesFragment newInstance(Workout workout)
    {
        ExercisesFragment fragment = new ExercisesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_WORKOUT, workout);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            workout = (Workout) getArguments().getSerializable(ARG_WORKOUT);
        }
        // Establecemos la top app bar cuando el fragmento ya se haya creado
        TopAppBar topAppBar = (TopAppBar) getParentFragment();
        topAppBar.swapToExercisesTopAppBar(this);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {
        View view = inflater.inflate(
            R.layout.fragment_exercises_list,
            container,
            false
        );

        exercisesList = view.findViewById(R.id.exercisesList);

        addBtn = view.findViewById(R.id.exercisesListAddBtn);
        workoutNameEditTextLayout = view.findViewById(R.id.exercisesListNameLayout);
        workoutNameEditText = view.findViewById(R.id.exercisesListName);
        restSwitch = view.findViewById(R.id.exercisesListRestSwitch);
        hint = view.findViewById(R.id.exercisesListHint);
        restMsg = view.findViewById(R.id.exercisesListRestMsg);

        setAddBtnListener();
        // Actualiza el valor del campo nombre del entrenamiento
        workoutNameEditText.setText(workout.getName());
        // Establecemos el listener del switch y actualizamos su valor
        initRestSwitch();
        // Si hay ejercicios en el entrenamiento o es día de descanso quitamos el texto informativo
        if (!workout.getExercises().isEmpty() || workout.isRestDay())
        {
            hint.setVisibility(View.GONE);
        }
        // Si es día de descanso ocultamos el RecyclerView, el campo de nombre y el botón
        // y hacemos visible el mensaje de descanso
        if (workout.isRestDay())
        {
            workoutNameEditTextLayout.setVisibility(View.INVISIBLE);
            exercisesList.setVisibility(View.INVISIBLE);
            addBtn.hide();
            restMsg.setVisibility(View.VISIBLE);
        }
        // Set the adapter
        if (exercisesList instanceof RecyclerView)
        {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) exercisesList;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new ExerciseAdapter(workout.getExercises(), listener));
        }

        return view;
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof OnExerciseSelectedListener)
        {
            listener = (OnExerciseSelectedListener) context;

        } else
        {
            throw new RuntimeException(
                context.toString() + " must implement OnExerciseSelectedListener"
            );
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    private void setAddBtnListener()
    {
        addBtn.setOnClickListener(v -> {
            Log.i("VGym", "Click en Añadir Ejercicio!");
        });
    }

    private void initRestSwitch()
    {
        restSwitch.setChecked(workout.isRestDay());

        restSwitch.setOnClickListener(v -> {
            workout.setRest(restSwitch.isChecked());

            if (restSwitch.isChecked())
            {
                Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                // Animación y callback para el EditTextLayout
                workoutNameEditTextLayout.startAnimation(fadeOut);
                workoutNameEditTextLayout.postOnAnimation(() -> {
                    workoutNameEditTextLayout.setVisibility(View.INVISIBLE);
                    // Hacemos un fade in al mensaje de descanso
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    restMsg.startAnimation(fadeIn);
                    restMsg.postOnAnimation(() -> restMsg.setVisibility(View.VISIBLE));
                });
                // RecyclerView
                exercisesList.startAnimation(fadeOut);
                exercisesList.postOnAnimation(() -> exercisesList.setVisibility(View.INVISIBLE));
                // Texto de información
                if (workout.getExercises().isEmpty())
                {
                    hint.startAnimation(fadeOut);
                    hint.postOnAnimation(() -> hint.setVisibility(View.INVISIBLE));
                }
                // Botón de añadir ejercicio
                addBtn.hide();
            }
            else
            {
                Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                restMsg.startAnimation(fadeOut);
                restMsg.postOnAnimation(() -> {
                    restMsg.setVisibility(View.GONE);
                    // Hacemos fade in a el resto de vistas
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    // Animación y callback para el EditTextLayout
                    workoutNameEditTextLayout.startAnimation(fadeIn);
                    workoutNameEditTextLayout.postOnAnimation(
                        () -> workoutNameEditTextLayout.setVisibility(View.VISIBLE)
                    );
                    // RecyclerView
                    exercisesList.startAnimation(fadeIn);
                    exercisesList.postOnAnimation(() -> exercisesList.setVisibility(View.VISIBLE));
                    // Texto de información
                    if (workout.getExercises().isEmpty())
                    {
                        hint.startAnimation(fadeIn);
                        hint.postOnAnimation(() -> hint.setVisibility(View.VISIBLE));
                    }
                    // Botón de añadir ejercicio
                    addBtn.show();
                });
            }
        });
    }

    public String getWorkoutDay() { return workout.getDay(); }

    /**
     * Establece el nombre del entrenamiento desde el campo nombre del entrenamiento.
     */
    public void setExerciseNameFromTextField()
    {
        workout.setName(workoutNameEditText.getText().toString());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnExerciseSelectedListener
    {
        void onExerciseSelected(Exercise exercise);
        void onDeleteExercise(Exercise exercise);
    }
}
