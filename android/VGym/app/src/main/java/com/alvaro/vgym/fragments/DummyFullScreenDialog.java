package com.alvaro.vgym.fragments;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.alvaro.vgym.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DummyFullScreenDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DummyFullScreenDialog extends DialogFragment
{
    public static String TAG = "dummyFullScreenDialog";

    public DummyFullScreenDialog() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DummyFullScreenDialog.
     */
    public static DummyFullScreenDialog newInstance()
    {
        DummyFullScreenDialog fragment = new DummyFullScreenDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(
            R.layout.dummy_full_screen_dialog,
            container,
            false
        );

        Toolbar toolbar = view.findViewById(R.id.dummyTopAppBar);
        toolbar.setNavigationOnClickListener(view1 -> dismiss());

        return view;
    }

    /** The system calls this only when creating the layout in a dialog. */
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

    /*@Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }*/
}
