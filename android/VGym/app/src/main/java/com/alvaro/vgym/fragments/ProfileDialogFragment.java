package com.alvaro.vgym.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.alvaro.vgym.R;
import com.alvaro.vgym.model.User;
import com.alvaro.vgym.services.FirebaseService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link ProfileDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileDialogFragment extends DialogFragment
{
    public static final String TAG = "profileDialogFragment";

    private static final String ARG_PARAM = "user";

    private User user;
    private MaterialToolbar topAppBar;

    private TextInputEditText nameInput;
    private TextInputEditText surnameInput;

    public ProfileDialogFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user The user.
     * @return A new instance of fragment ProfileDialogFragment.
     */
    public static ProfileDialogFragment newInstance(User user)
    {
        ProfileDialogFragment fragment = new ProfileDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, user);
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
            user = (User) getArguments().getSerializable(ARG_PARAM);
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
            R.layout.fragment_profile_dialog,
            container,
            false
        );

        topAppBar = view.findViewById(R.id.fragmentTopAppBar);
        setTopAppBarListener();

        nameInput = view.findViewById(R.id.profileName);
        surnameInput = view.findViewById(R.id.profileSurname);

        nameInput.setText(user.getName());
        surnameInput.setText(user.getSurName());

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

    private void setTopAppBarListener()
    {
        topAppBar.setNavigationOnClickListener(v -> dismiss());

        topAppBar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId())
            {
                case R.id.mnuWorkoutListSave:
                    if (validate())
                    {
                        user.setName(nameInput.getText().toString());
                        user.setSurName(surnameInput.getText().toString());

                        FirebaseService firebaseService = FirebaseService.getInstance();

                        String reference = "users/" + firebaseService.getUid();

                        firebaseService.save(reference, user);

                        dismiss();
                    }
                    else
                    {
                        Snackbar.make(
                            topAppBar,
                            R.string.fields_empty,
                            BaseTransientBottomBar.LENGTH_SHORT
                        ).show();
                    }

                    break;
            }

            return false;
        });
    }

    private boolean validate()
    {
        String name = nameInput.getText().toString();
        String surname = surnameInput.getText().toString();

        return (!name.trim().isEmpty() && !surname.trim().isEmpty());
    }
}
