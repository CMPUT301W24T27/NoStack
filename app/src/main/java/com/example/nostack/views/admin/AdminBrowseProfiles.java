package com.example.nostack.views.admin;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.controllers.EventController;
import com.example.nostack.models.Event;
import com.example.nostack.models.Image;
import com.example.nostack.models.User;
import com.example.nostack.views.event.adapters.EventArrayAdapter;
import com.example.nostack.viewmodels.UserViewModel;
import com.example.nostack.views.user.UserArrayAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Creates the AdminBrowseProfiles fragment which is used to display all the users
 */
public class AdminBrowseProfiles extends Fragment {
    private UserArrayAdapter UserArrayAdapter;
    private ListView userList;
    private ArrayList<User> dataList;
    private UserViewModel userViewModel;
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
        dataList = new ArrayList<>();

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
        UserArrayAdapter = new UserArrayAdapter(getContext(), dataList, this);
        userList.setAdapter(UserArrayAdapter);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Watch for errors
        userViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                userViewModel.clearErrorLiveData();
            }
        });

        // Fetch and get users
        userViewModel.fetchAllUsers();
        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            UserArrayAdapter.clear();
            for (User user : users) {
                UserArrayAdapter.addUser(user);
            }
            UserArrayAdapter.notifyDataSetChanged();
        });

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                showDialog(UserArrayAdapter.getUser(position));
                Toast.makeText(getActivity(), "the item was at position: " + position , Toast.LENGTH_SHORT).show();
            }
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
        TextView userGender = dialog.findViewById(R.id.admin_userDialogGender);


        if (user.getUsername() != null){
            userTitle.setText(user.getUsername());
        } else {
            userTitle.setText("Username: N/A");
        }
        userEmail.setText("Email: " + user.getEmailAddress());
        userPhoneNumber.setText("Phone Number: " + user.getPhoneNumber());
        userRole.setText("Role: " + user.getRole());
        userFirstName.setText("First Name: " + user.getFirstName());
        userLastName.setText("Last Name: " + user.getLastName());
        userUUID.setText("Uuid: " + user.getUuid());
        userGender.setText("Gender: " + user.getGender());

        String uri = user.getProfileImageUrl();

        if (uri != null) {
            Log.w("User Profile - uri not null", uri);
            // Get image from firebase storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
            final long ONE_MEGABYTE = 1024 * 1024;

            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 250, 250, false);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getActivity().getResources(), scaledBmp);
                d.setCornerRadius(50f);
                userBanner.setImageDrawable(d);
            }).addOnFailureListener(exception -> {
                Log.w("User Profile", "Error getting User banner", exception);
            });
        }
    }

}
