package com.example.nostack.model.Events;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.example.nostack.AttendeeArrayAdapter;
import com.example.nostack.R;
import com.example.nostack.model.User.User;
import com.example.nostack.utils.Attendance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.DataTruncation;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventAttendeesArrayAdapter extends ArrayAdapter<Attendance> {

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private ArrayList<Attendance> attendances;
    public EventAttendeesArrayAdapter(@NonNull Context context, ArrayList<Attendance> attendances, Fragment currfragment) {
        super(context, 0,attendances);
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        this.attendances = attendances;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_attendee_list_content,parent,false);
        } else {
            view = convertView;
        }

        Attendance attendance =  getItem(position);
        TextView attendeeName = view.findViewById(R.id.event_attendee_name);
        TextView numCheckins = view.findViewById(R.id.event_attendee_num_checkins);

        DocumentReference userDocRef =  usersRef.document(attendance.getUserId());
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User docUser = document.toObject(User.class);
                        numCheckins.setText(String.valueOf(attendance.getNumCheckIn()));
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

    public boolean containsAttendance(Attendance attendance) {
        for (Attendance at:attendances) {
            if (attendance.getId().equals(at.getId())) {
                return true;
            }
        }
        return false;
    }

    public void addAttendance(Attendance att) {
        add(att);
    }
}
