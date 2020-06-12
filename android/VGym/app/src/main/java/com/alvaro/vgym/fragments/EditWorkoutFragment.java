package com.alvaro.vgym.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alvaro.vgym.R;
import com.alvaro.vgym.adapters.EditWorkoutAdapter;
import com.alvaro.vgym.model.Exercise;
import com.alvaro.vgym.model.Workout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnExerciseSelectedListener}
 * interface.
 */
public class EditWorkoutFragment extends Fragment
{
    public static final String TAG = "exercisesFragment";
    private static final String ARG_WORKOUT = "workout";

    private Workout workout = null;
    private MaterialToolbar topAppBar;

    private EditWorkoutAdapter adapter;


    private View exercisesList;

    private SwitchMaterial restSwitch;
    private TextInputLayout nameInputLayout;
    private TextInputEditText nameInput;
    private ExtendedFloatingActionButton addBtn;

    private TextView hint;

    private LinearLayout restMsg;

    private OnExerciseSelectedListener listener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditWorkoutFragment() { }

    @SuppressWarnings("unused")
    public static EditWorkoutFragment newInstance(Workout workout)
    {
        EditWorkoutFragment fragment = new EditWorkoutFragment();
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

            if (workout.getExercises() == null) { workout.setExercises(new ArrayList<Exercise>()); }
        }
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {
        View view = inflater.inflate(
            R.layout.fragment_edit_workout_layout,
            container,
            false
        );

        exercisesList = view.findViewById(R.id.fragmentRecyclerView);

        topAppBar = view.findViewById(R.id.fragmentTopAppBar);
        topAppBar.setTitle(workout.getDay());
        setTopAppBarListener();

        nameInputLayout = view.findViewById(R.id.editWorkoutNameLayout);
        nameInput = view.findViewById(R.id.editWorkoutName);
        // Actualiza el valor del campo nombre del entrenamiento
        nameInput.setText(workout.getName());

        restSwitch = view.findViewById(R.id.editWorkoutRestSwitch);
        initRestSwitch(); // Inicializa el componente Switch

        hint = view.findViewById(R.id.editWorkoutHint);

        restMsg = view.findViewById(R.id.editWorkoutRestMsg);

        addBtn = view.findViewById(R.id.editWorkoutAddBtn);
        setAddBtnListener(); // Establece el botón de añadir ejercicio
        // Si hay ejercicios en el entrenamiento o es día de descanso quitamos el texto informativo
        if (!workout.getExercises().isEmpty() || workout.isRestDay())
        {
            hint.setVisibility(View.GONE);
        }
        // Si es día de descanso ocultamos el RecyclerView, el campo de nombre y el botón
        // y hacemos visible el mensaje de descanso
        if (workout.isRestDay())
        {
            nameInputLayout.setVisibility(View.INVISIBLE);
            exercisesList.setVisibility(View.INVISIBLE);
            restMsg.setVisibility(View.VISIBLE);
            addBtn.hide();
        }
        // Set the adapter
        if (exercisesList instanceof RecyclerView)
        {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) exercisesList;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            adapter = new EditWorkoutAdapter(workout.getExercises(), listener);
            recyclerView.setAdapter(adapter);
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

    private void setTopAppBarListener()
    {   // Listener del botón de navegar a la pantalla anterior
        topAppBar.setNavigationOnClickListener(v -> {
            // Establecemos el nombre del entrenamiento
            workout.setName(nameInput.getText().toString());
            // Retroceder a la pantalla de la lista de días
            getFragmentManager().popBackStack();
        });
    }

    /**
     * Establece el listener del botón añadir ejercicio.
     */
    private void setAddBtnListener()
    {
        addBtn.setOnClickListener(v -> {
            disableDialog();

            ExerciseDialogFragment dialog = ExerciseDialogFragment.newInstance(
                new Exercise(workout.getExercises().size(), "", 0, 0)
            );

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            dialog.show(fragmentManager, ExerciseDialogFragment.TAG);
        });
    }

    /**
     * Inicializa el componente Switch.
     */
    private void initRestSwitch()
    {
        restSwitch.setChecked(workout.isRestDay());

        restSwitch.setOnClickListener(v -> {
            workout.setRestDay(restSwitch.isChecked());

            if (restSwitch.isChecked())
            {
                nameInputLayout.setEnabled(false);
                exercisesList.setEnabled(false);

                Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                // Animación y callback para el EditTextLayout
                nameInputLayout.startAnimation(fadeOut);
                nameInputLayout.postOnAnimation(() -> {
                    nameInputLayout.setVisibility(View.INVISIBLE);
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

                    nameInputLayout.setEnabled(true);
                    exercisesList.setEnabled(true);
                    // Hacemos fade in a el resto de vistas
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    // Animación y callback para el EditTextLayout
                    nameInputLayout.startAnimation(fadeIn);
                    nameInputLayout.postOnAnimation(
                        () -> nameInputLayout.setVisibility(View.VISIBLE)
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

    /**
     * Añade un ejercicio a la lista de ejercicios.
     *
     * @param exercise El ejercicio.
     */
    public void addExercise(Exercise exercise)
    {
        workout.getExercises().add(exercise);
        // Actualizamos el estado del texto informativo
        if (!workout.getExercises().isEmpty()) { hint.setVisibility(View.GONE); }
    }

    public void removeExercise(Exercise exercise)
    {
        List<Exercise> exercises = workout.getExercises();
        Exercise highestId = exercises.get(0);
        // Obtenemos el ejercicio con la id más grande
        for (Exercise listExercise : exercises)
        {
            if (listExercise.getId() > highestId.getId()) { highestId = listExercise; }
        }

        if (exercise.equals(highestId)) { exercises.remove(exercise); }
        else
        {
            int id = exercise.getId();
            exercises.remove(exercise);
            highestId.setId(id);
        }

        adapter.notifyDataSetChanged();
        // Actualizamos el estado del texto informativo
        if (workout.getExercises().isEmpty()) { hint.setVisibility(View.VISIBLE); }
    }

    public void enableDialogAndUpdateData()
    {   // Notificamos al adaptador que se actualice ya que se han cambiado los datos.
        adapter.notifyDataSetChanged();

        setTopAppBarListener();
        adapter.setEnabled(true);
        exercisesList.setEnabled(true);
        restSwitch.setEnabled(true);
        addBtn.setEnabled(true);

        if (nameInputLayout.getVisibility() == View.VISIBLE) { nameInputLayout.setEnabled(true); }
    }

    public void disableDialog()
    {
        topAppBar.setNavigationOnClickListener(null);
        adapter.setEnabled(false);
        exercisesList.setEnabled(false);
        restSwitch.setEnabled(false);
        addBtn.setEnabled(false);

        if (nameInputLayout.getVisibility() == View.VISIBLE) { nameInputLayout.setEnabled(false); }
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
        void onExerciseSelected(Exercise exercise, boolean enabled);
        void onDeleteExercise(Exercise exercise, boolean enabled);
    }
}
