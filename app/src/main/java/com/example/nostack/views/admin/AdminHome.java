package com.example.nostack.views.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nostack.R;
import com.example.nostack.viewmodels.UserViewModel;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminHome extends Fragment {

    private UserViewModel userViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ViewPager2 viewPager;
    private DotsIndicator dotsIndicator;
    private static final Class[] fragments = new Class[]{AdminBrowseEvents.class, AdminBrowseProfiles.class};

    public AdminHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminHome.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminHome newInstance(String param1, String param2) {
        AdminHome fragment = new AdminHome();
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

        userViewModel = new ViewModelProvider((AppCompatActivity) getActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        TextView userWelcome = (TextView) view.findViewById(R.id.text_userWelcome);
        viewPager = view.findViewById(R.id.admin_viewPager2);
        Log.d("AdminHome", "viewPager:"+ viewPager);
        viewPager.setAdapter(new MyFragmentAdapter(this));
//        dotsIndicator = view.findViewById(R.id.admin_dots_indicator);
//        dotsIndicator.attachTo(viewPager);

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                Log.d("AdminHome", "User logged in: " + user.getUsername());
                userWelcome.setText(user.getUsername());

            } else {
                Log.d("AdminHome", "User is null");
            }
        });

        view.findViewById(R.id.admin_profileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminHome.this)
                        .navigate(R.id.action_adminHome_to_userProfile);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }
    private static class MyFragmentAdapter extends FragmentStateAdapter {

        public MyFragmentAdapter(Fragment fragment) {
            super(fragment);
        }

        /**
         * This method is called when the fragment is being created and then sets up the view for the fragment
         *
         * @param position The position of the fragment
         * @return a null fragment if there is an exception
         */
        @Override
        public Fragment createFragment(int position) {
            try {
                return (Fragment) fragments[position].newInstance();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public int getItemCount() {
            return fragments.length;
        }
    }
}