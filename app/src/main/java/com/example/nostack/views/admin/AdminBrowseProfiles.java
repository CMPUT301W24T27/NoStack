package com.example.nostack.views.admin;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.example.nostack.controllers.EventController;
import com.example.nostack.handlers.ImageViewHandler;
import com.example.nostack.models.User;
import com.example.nostack.services.SkeletonProvider;
import com.example.nostack.viewmodels.UserViewModel;
import com.example.nostack.views.admin.adapters.ProfileArrayRecycleViewAdapter;
import com.example.nostack.views.admin.adapters.ProfileArrayRecycleViewInterface;
import com.example.nostack.views.user.UserArrayAdapter;
import com.faltenreich.skeletonlayout.Skeleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Creates the AdminBrowseProfiles fragment which is used to display all the users
 */
public class AdminBrowseProfiles extends Fragment {
    private ProfileArrayRecycleViewAdapter UserArrayAdapter;
    private AllUsers allUsers;
    private RecyclerView userList;
    private ArrayList<User> dataList;
    private UserViewModel userViewModel;
    private ImageViewHandler imageViewHandler;
    private Skeleton skeleton;
    public AdminBrowseProfiles() {
    }

    /**
     * This method is called when the fragment is being created and then sets up the variables for the view
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        allUsers = new AllUsers();
        dataList = new ArrayList<>();
        imageViewHandler = ImageViewHandler.getSingleton();
    }

    /**
     * This method is called when the fragment is being created and then sets up the view for the fragment
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Returns the modified view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment only once
        View view = inflater.inflate(R.layout.fragment_admin_home_browseusers, container, false);
        userList = view.findViewById(R.id.admin_viewPager2);
        UserArrayAdapter = new ProfileArrayRecycleViewAdapter(getContext(), dataList, this, new ProfileArrayRecycleViewInterface() {
            @Override
            public void onProfileClick(int position) {
                showDialog(UserArrayAdapter.getUser(position));
            }
        });
        userList.setItemViewCacheSize(100);
        userList.setLayoutManager(new LinearLayoutManager(getContext()));
        userList.setAdapter(UserArrayAdapter);

        skeleton = SkeletonProvider.getSingleton().adminProfileSkeleton(userList);
        skeleton.showSkeleton();
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Watch for errors
        allUsers.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                allUsers.clearErrorLiveData();
            }
        });

        // Fetch and get users
        allUsers.fetchAllUsers();
        allUsers.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            UserArrayAdapter.clear();
            for (User user : users) {
                Log.d("AdminBrowseProfiles", user.getUuid() + " " + user.getRole());
                UserArrayAdapter.addUser(user);
            }
            UserArrayAdapter.notifyDataSetChanged();
            skeleton.showOriginal();
        });
    }
    private void showDialog(User user){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.fragment_admin_users_dialog);
        dialog.show();

        ImageView userBanner = dialog.findViewById(R.id.admin_userDialog);
        TextView userTitle = dialog.findViewById(R.id.admin_userDialogTitle);
        TextView userEmail = dialog.findViewById(R.id.admin_userDialogEmail);
        TextView userPhoneNumber = dialog.findViewById(R.id.admin_userDialogPhoneNumber);
        TextView userRole = dialog.findViewById(R.id.admin_userDialogRole);
        TextView userFirstName = dialog.findViewById(R.id.admin_userDialogFirstName);
        TextView userLastName = dialog.findViewById(R.id.admin_userDialogLastName);
        TextView userUUID = dialog.findViewById(R.id.admin_userDialogUUID);
        FloatingActionButton deleteUserButton = dialog.findViewById(R.id.admin_deleteUserButton);
        Button makeAdminButton = dialog.findViewById(R.id.admin_amkeAdminUserButton);

        if (user.getUsername() != null){
            userTitle.setText(user.getUsername());
        } else {
            userTitle.setText("Username: N/A");
        }
        userEmail.setText("Email: " + user.getEmailAddress() == null ? "N/A" : user.getEmailAddress());
        userPhoneNumber.setText("Phone Number: " + user.getPhoneNumber() == null ? "N/A" : user.getPhoneNumber());
        userRole.setText("Role: " + (user.getRole() == null ? "User" : user.getRole().substring(0, 1).toUpperCase() + user.getRole().substring(1)));
        userFirstName.setText(user.getFirstName());
        userLastName.setText(user.getLastName());
        userUUID.setText("UUID: " + user.getUuid());

        boolean isAdmin = user.getRole() != null && user.getRole().contains( "admin");

        if(isAdmin){
            makeAdminButton.setText("Remove Admin");
            userRole.setText("Role: Admin");
        } else {
            makeAdminButton.setText("Make Admin");
        }

        imageViewHandler.setUserProfileImage(user, userBanner,getResources(), null);

        deleteUserButton.setOnClickListener(v -> {
            if(deleteProfile(user)){
                // Update view after deleting user
                dataList.remove(user);
                UserArrayAdapter.notifyDataSetChanged();
                // Close dialog
                dialog.dismiss();
                Toast.makeText(getContext(), "User deleted.", Toast.LENGTH_SHORT).show();
            }
        });

        makeAdminButton.setOnClickListener(v -> {
            if(user.getRole() == null){
                makeAdmin(user);
                userRole.setText("Role: Admin");
                Toast.makeText(getContext(), "User is now an admin.", Toast.LENGTH_SHORT).show();
                makeAdminButton.setText("Remove Admin");
            } else {
                removeAdmin(user);
                userRole.setText("Role: User");
                Toast.makeText(getContext(), "User is no longer an admin.", Toast.LENGTH_SHORT).show();
                makeAdminButton.setText("Make Admin");
            }
        });
    }

    /**
     * Delete user profile
     * @param user
     * @return true if user is deleted, false otherwise
     */
    private boolean deleteProfile(User user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection("users");
        Query query = users.whereEqualTo("uuid", user.getUuid());

        // Check if it is the current user
        if (user.getUuid().equals(userViewModel.getUser().getValue().getUuid())) {
            Toast.makeText(getContext(), "Cannot delete yourself.", Toast.LENGTH_SHORT).show();
            return false;
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Delete user events
                EventController eventController = EventController.getInstance();
                eventController.deleteEventsByUser(user.getUuid()).addOnSuccessListener(aVoid -> {
                    Log.d("Delete User", "User events successfully deleted.");
                }).addOnFailureListener(e -> {
                    Log.e("Delete User", "Failed to delete user events.", e);
                });

                // Delete user
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }

                // Delete user profile image
                if(user.getProfileImageUrl() != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl(user.getProfileImageUrl());
                    storageRef.delete().addOnSuccessListener(aVoid -> {
                        Log.d("Delete User", "User profile image successfully deleted.");
                    }).addOnFailureListener(e -> {
                        Log.e("Delete User", "Failed to delete user profile image.", e);
                    });
                }
            } else {
                Log.d("Delete User", "Error getting documents: ", task.getException());
            }
        });

        return true;
    }

    /**
     * Make user an admin
     * @param user
     */
    private void makeAdmin(User user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection("users");
        Query query = users.whereEqualTo("uuid", user.getUuid());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().update("role", "admin");
                    user.setRole("admin");
                }
            } else {
                Log.d("Make Admin", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Remove admin role from user
     * @param user
     */
    private void removeAdmin(User user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection("users");
        Query query = users.whereEqualTo("uuid", user.getUuid());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().update("role", null);
                    user.setRole(null);
                }
            } else {
                Log.d("Remove Admin", "Error getting documents: ", task.getException());
            }
        });
    }
}
