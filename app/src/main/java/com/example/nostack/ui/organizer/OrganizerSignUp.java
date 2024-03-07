package com.example.nostack.ui.organizer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nostack.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerSignUp#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerSignUp extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrganizerSignUp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerSignUp.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerSignUp newInstance(String param1, String param2) {
        OrganizerSignUp fragment = new OrganizerSignUp();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_sign_up, container, false);

        view.findViewById(R.id.SignUpConfirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TODO: ADD CODE CONTAINING ADDING USER TO DATA BASE / DATA VALIDATION ETC...


                NavHostFragment.findNavController(OrganizerSignUp.this)
                        .navigate(R.id.action_organizerSignUp_to_organizerHome);
            }
        });

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerSignUp.this)
                        .popBackStack();
            }
        });

        return view;
     }
}