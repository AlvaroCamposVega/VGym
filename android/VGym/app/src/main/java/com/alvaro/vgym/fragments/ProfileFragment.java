package com.alvaro.vgym.fragments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alvaro.vgym.R;
import com.alvaro.vgym.adapters.ProfileAdapter;
import com.alvaro.vgym.database.RoutineCacheContract.*;
import com.alvaro.vgym.model.Routine;
import com.alvaro.vgym.model.User;
import com.alvaro.vgym.services.FirebaseService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRoutineInteractionListener}
 * interface.
 */
public class ProfileFragment extends Fragment
{
    public static final String TAG = "profileFragment";

    private List<Routine> routines;
    private User user;
    private MaterialToolbar topAppBar;
    private ProfileAdapter adapter;
    private FirebaseService firebaseService;

    private View view;
    private TextView loading;
    private TextView userName;
    private Button editBtn;
    private NestedScrollView noRoutines;

    private OnRoutineInteractionListener listener;

    private boolean onAccountDeleted;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProfileFragment() { }

    @SuppressWarnings("unused")
    public static ProfileFragment newInstance() { return new ProfileFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        firebaseService = FirebaseService.getInstance();

        onAccountDeleted = false;
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    )
    {
        view = inflater.inflate(
            R.layout.fragment_profile_layout,
            container,
            false
        );

        topAppBar = view.findViewById(R.id.fragmentTopAppBar);
        setTopAppBarListener();

        userName = view.findViewById(R.id.profileName);

        editBtn = view.findViewById(R.id.profileEditBtn);
        setEditBtnListener();

        loading = view.findViewById(R.id.profileLoading);
        noRoutines = view.findViewById(R.id.profileNoRoutines);

        getData();

        return view;
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof OnRoutineInteractionListener)
        {
            listener = (OnRoutineInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                + " must implement OnRoutineInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    /**
     * Establece el listener de la top app bar para este fragmento.
     */
    private void setTopAppBarListener()
    {   // Listener
        topAppBar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId())
            {
                case R.id.mnuProfileDeleteAccount:
                    new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.caution)
                        .setMessage(R.string.delete_account_confirmation_text)
                        .setNeutralButton(R.string.cancel_label, null)
                        .setPositiveButton(R.string.ok, (dialog, which) -> {
                            String uid = firebaseService.getUid();
                            String userRef = "users/" + uid;
                            String routinesRef = "routines/" + uid;
                            String selectedRef = "selected/" + uid;

                            DatabaseReference dbUserRef = firebaseService
                                .getReference(userRef);
                            DatabaseReference dbRoutinesRef = firebaseService
                                .getReference(routinesRef);
                            DatabaseReference dbSelectedRef = firebaseService
                                .getReference(selectedRef);

                            firebaseService.deleteUser().addOnCompleteListener(task -> {
                                if (task.isSuccessful())
                                {
                                    onAccountDeleted = true;

                                    dbUserRef.removeValue();
                                    dbRoutinesRef.removeValue();
                                    dbSelectedRef.removeValue();

                                    getActivity().finish(); // Finalizamos la actividad
                                }
                                else
                                {
                                    Snackbar.make(
                                        topAppBar,
                                        R.string.delete_account_needs_restart,
                                        BaseTransientBottomBar.LENGTH_LONG
                                    ).show();
                                }
                            });
                        })
                        .show();

                    break;
                case R.id.mnuProfileLogOut:
                    // Cerramos la sesión
                    firebaseService.signOut();
                    getActivity().finish(); // Finalizamos la actividad

                    break;
                case R.id.mnuProfileClearCache:
                    RoutineCacheDbHelper dbHelper = new RoutineCacheDbHelper(getContext());

                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    db.delete(RoutineCache.TABLE_NAME, null, null);
                    break;
            }

            return false;
        });
    }

    private void setEditBtnListener()
    {
        editBtn.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            ProfileDialogFragment profileDialog = ProfileDialogFragment.newInstance(user);

            profileDialog.show(fragmentManager, ProfileDialogFragment.TAG);
        });
    }

    private void getData()
    {
        String uid = firebaseService.getUid();

        DatabaseReference dbUserRef = firebaseService.getReference("users/" + uid);

        dbUserRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!onAccountDeleted)
                {
                    user = dataSnapshot.getValue(User.class);

                    String reference = "routines/" + uid;
                    DatabaseReference dbRoutinesRef = firebaseService.getReference(reference);

                    dbRoutinesRef.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            routines = new ArrayList<Routine>();

                            if (dataSnapshot.hasChildren())
                            {
                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren())
                                {
                                    routines.add(childDataSnapshot.getValue(Routine.class));
                                }

                                setView(true);
                            }
                            else { setView(false); }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setView(boolean hasRoutines)
    {
        View routineList = view.findViewById(R.id.fragmentRecyclerView);

        loading.setVisibility(View.GONE);
        userName.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.VISIBLE);

        topAppBar.setTitle(firebaseService.getEmail());
        userName.setText(user.toString());

        if (hasRoutines)
        {
            noRoutines.setVisibility(View.GONE);
            routineList.setVisibility(View.VISIBLE);
            // Set the adapter
            if (routineList instanceof RecyclerView)
            {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) routineList;
                adapter = new ProfileAdapter(routines, listener);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(adapter);
            }
        }
        else { noRoutines.setVisibility(View.VISIBLE); routineList.setVisibility(View.GONE); }
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
    public interface OnRoutineInteractionListener
    {
        void onEditRoutine(Routine routine);
        void onAddRoutineToFavorites(Routine routine);
        void onChangeSelectedRoutine(Routine routine);
        void onDeleteRoutine(String id, List<Routine> routines);
    }
}
