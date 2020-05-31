package com.alvaro.vgym.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alvaro.vgym.R;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewRoutineName#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewRoutineName extends Fragment
{
    public static final String TAG = "newRoutineName";

    private TextInputEditText nameEditText;

    // Required empty public constructor
    public NewRoutineName() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewRoutineName.
     */
    public static NewRoutineName newInstance()
    {
        NewRoutineName fragment = new NewRoutineName();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {
        View view = inflater.inflate(
            R.layout.fragment_new_routine_name,
            container,
            false
        );

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        NewRoutineDialogFragment dialogFragment = (NewRoutineDialogFragment) fragmentManager
            .findFragmentByTag(NewRoutineDialogFragment.TAG);

        nameEditText = view.findViewById(R.id.newRoutineName);
        nameEditText.setText(dialogFragment.getRoutineName());

        return view;
    }

    public String getRoutineNameFromTextField() { return nameEditText.getText().toString(); }
}