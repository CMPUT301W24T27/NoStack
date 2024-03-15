package com.example.nostack.views.event.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nostack.R;
import com.example.nostack.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventRegisteredArrayAdapter extends ArrayAdapter<String> {

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private ArrayList<String> userIds;
    public EventRegisteredArrayAdapter(@NonNull Context context, ArrayList<String> userIds, Fragment currfragment) {
        super(context, 0,userIds);
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        this.userIds = userIds;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_registered_list_content,parent,false);
        } else {
            view = convertView;
        }

        String userId =  getItem(position);
        TextView attendeeName = view.findViewById(R.id.event_attendee_name);
        TextView numCheckins = view.findViewById(R.id.event_attendee_num_checkins);

        DocumentReference userDocRef =  usersRef.document(userId);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User docUser = document.toObject(User.class);
                        attendeeName.setText(docUser.getFirstName() + " " + docUser.getLastName());
                    } else {
                        Log.d("Attendee Event List", "User does not exist");
                    }
                } else {
                    Log.d("Attendee Event List", "Could not retrieve user.");
                }
            }
        });
        return view;
    }
    public void addAttendance(String uid) {
        if (userIds.contains(uid)) {
            userIds.add(uid);
        }

    }
}
