package com.alvaro.vgym.fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.alvaro.vgym.R;
import com.alvaro.vgym.database.RoutineCacheContract.*;
import com.alvaro.vgym.model.Routine;
import com.alvaro.vgym.model.Workout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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

    @Override
    public void onDismiss(@NonNull DialogInterface dialog)
    {
        if (routine.getId().isEmpty())
        {
            boolean nameNotEmpty = (!routine.getName().trim().isEmpty());
            boolean workoutNotEmpty = false;

            for (Workout workout : routine.getWorkouts())
            {
                if (!workout.getName().trim().isEmpty() || !workout.getExercises().isEmpty())
                {
                    workoutNotEmpty = true;
                }
            }

            if (nameNotEmpty || workoutNotEmpty)
            {
                RoutineCacheDbHelper dbHelper = new RoutineCacheDbHelper(getContext());

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                db.delete(RoutineCache.TABLE_NAME, null, null);

                ContentValues values = new ContentValues();
                values.put(RoutineCache.COLUMN_NAME_ROUTINE, makeByte());

                db.insert(RoutineCache.TABLE_NAME, null, values);
            }
        }

        super.onDismiss(dialog);
    }

    private byte[] makeByte()
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(routine);

            byte[] routineAsBytes = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(routineAsBytes);

            return routineAsBytes;
        }
        catch (IOException e) { e.printStackTrace(); }

        return null;
    }
}
